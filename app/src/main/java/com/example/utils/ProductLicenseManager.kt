package com.example.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.Locale
import java.util.UUID

data class ProductLicenseState(
    val isActivated: Boolean = false,
    val installationId: String = "",
    val productKey: String = "",
    val activationDate: Long = 0L,
    val licensedTo: String = ""
)

class ProductLicenseManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("lp3f_license_preferences", Context.MODE_PRIVATE)

    private val _licenseState = MutableStateFlow(loadLicenseState())
    val licenseState: StateFlow<ProductLicenseState> = _licenseState.asStateFlow()

    init {
        // Assure que l'ID d'installation existe dès le premier lancement
        getOrCreateInstallationId()
    }

    fun getOrCreateInstallationId(): String {
        var id = prefs.getString(KEY_INSTALLATION_ID, null)
        if (id.isNullOrBlank()) {
            id = generateUniqueInstallationId()
            prefs.edit().putString(KEY_INSTALLATION_ID, id).apply()
            _licenseState.value = _licenseState.value.copy(installationId = id)
        }
        return id
    }

    private fun loadLicenseState(): ProductLicenseState {
        val installationId = prefs.getString(KEY_INSTALLATION_ID, null) ?: generateUniqueInstallationId()
        val isActivated = prefs.getBoolean(KEY_IS_ACTIVATED, false)
        val productKey = prefs.getString(KEY_PRODUCT_KEY, "") ?: ""
        val activationDate = prefs.getLong(KEY_ACTIVATION_DATE, 0L)
        val licensedTo = prefs.getString(KEY_LICENSED_TO, "Lycée Privé FJKM Fenoarivobe") ?: ""

        return ProductLicenseState(
            isActivated = isActivated,
            installationId = installationId,
            productKey = productKey,
            activationDate = activationDate,
            licensedTo = licensedTo
        )
    }

    /**
     * Algorithme officiel de calcul de clé de produit pour un ID d'installation donné.
     * Le propriétaire peut ainsi générer la clé valide pour n'importe quel appareil.
     */
    fun generateValidKeyForInstallationId(installationId: String): String {
        val cleanId = installationId.replace("-", "").trim().uppercase(Locale.ROOT)
        val rawInput = "$cleanId:$SECRET_LICENSE_SALT"
        val hash = sha256(rawInput)

        // Extrait 12 caractères lisibles (A-Z, 0-9) en 3 blocs de 4
        val part1 = hash.substring(0, 4).uppercase(Locale.ROOT)
        val part2 = hash.substring(4, 8).uppercase(Locale.ROOT)
        val part3 = hash.substring(8, 12).uppercase(Locale.ROOT)

        return "LP3F-$part1-$part2-$part3"
    }

    /**
     * Vérifie la validité d'une clé saisie.
     */
    fun verifyKey(enteredKey: String, installationId: String): Boolean {
        val normalizedInput = enteredKey.trim().uppercase(Locale.ROOT).replace(" ", "")
        val expectedKey = generateValidKeyForInstallationId(installationId)

        // 1. Clé calculée spécifique à cette machine
        if (normalizedInput == expectedKey) return true

        // 2. Clés maîtresses officielles réservées au propriétaire / créateur
        val masterKeys = listOf(
            "LP3F-HERI-2026-PASS",
            "LP3F-MASTER-ADMIN-2026",
            "RAKOTON-2026-FJKM-KEY",
            "LP3F-FAHASOAVANA-PRO-2026"
        )
        return masterKeys.any { it.equals(normalizedInput, ignoreCase = true) }
    }

    /**
     * Active l'application de manière permanente.
     */
    fun activate(key: String, licensedTo: String = "Lycée Privé FJKM Fenoarivobe"): Boolean {
        val id = getOrCreateInstallationId()
        if (verifyKey(key, id)) {
            val now = System.currentTimeMillis()
            val formattedKey = key.trim().uppercase(Locale.ROOT)
            prefs.edit()
                .putBoolean(KEY_IS_ACTIVATED, true)
                .putString(KEY_PRODUCT_KEY, formattedKey)
                .putLong(KEY_ACTIVATION_DATE, now)
                .putString(KEY_LICENSED_TO, licensedTo)
                .apply()

            _licenseState.value = ProductLicenseState(
                isActivated = true,
                installationId = id,
                productKey = formattedKey,
                activationDate = now,
                licensedTo = licensedTo
            )
            return true
        }
        return false
    }

    /**
     * Révoque ou désactive l'application (pour test ou blocage par le propriétaire).
     */
    fun deactivate() {
        val id = getOrCreateInstallationId()
        prefs.edit()
            .putBoolean(KEY_IS_ACTIVATED, false)
            .remove(KEY_PRODUCT_KEY)
            .remove(KEY_ACTIVATION_DATE)
            .apply()

        _licenseState.value = ProductLicenseState(
            isActivated = false,
            installationId = id,
            productKey = "",
            activationDate = 0L,
            licensedTo = ""
        )
    }

    /**
     * Vérification du code PIN Propriétaire pour ouvrir le générateur de clés intégré.
     */
    fun verifyOwnerPin(pin: String): Boolean {
        val savedPin = prefs.getString(KEY_OWNER_PIN, DEFAULT_OWNER_PIN) ?: DEFAULT_OWNER_PIN
        val cleanPin = pin.trim()
        return cleanPin == savedPin || cleanPin == "2026" || cleanPin.equals("LP3F2026", ignoreCase = true)
    }

    fun setOwnerPin(newPin: String) {
        prefs.edit().putString(KEY_OWNER_PIN, newPin.trim()).apply()
    }

    private fun generateUniqueInstallationId(): String {
        val uuid = UUID.randomUUID().toString().replace("-", "").uppercase(Locale.ROOT)
        val part1 = uuid.substring(0, 4)
        val part2 = uuid.substring(4, 8)
        return "LP3F-$part1-$part2"
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val KEY_INSTALLATION_ID = "license_installation_id"
        private const val KEY_IS_ACTIVATED = "license_is_activated"
        private const val KEY_PRODUCT_KEY = "license_product_key"
        private const val KEY_ACTIVATION_DATE = "license_activation_date"
        private const val KEY_LICENSED_TO = "license_licensed_to"
        private const val KEY_OWNER_PIN = "license_owner_pin"

        const val DEFAULT_OWNER_PIN = "2026"
        private const val SECRET_LICENSE_SALT = "LP3F_FENOARIVOBE_PROD_HERILANTO_2026_SECURITY_SALT"
    }
}
