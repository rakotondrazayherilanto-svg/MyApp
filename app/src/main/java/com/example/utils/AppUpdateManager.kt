package com.example.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast

data class InstalledAppItem(
    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean,
    val lastUpdateTime: Long,
    val icon: Drawable?
)

object AppUpdateManager {

    /**
     * Liste toutes les applications installées sur l'appareil.
     */
    fun getInstalledApps(context: Context): List<InstalledAppItem> {
        val pm = context.packageManager
        val packages: List<PackageInfo> = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
            } else {
                pm.getInstalledPackages(0)
            }
        } catch (e: Exception) {
            emptyList()
        }

        val list = mutableListOf<InstalledAppItem>()
        for (pkg in packages) {
            val appInfo = pkg.applicationInfo ?: continue
            val appName = try {
                pm.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                pkg.packageName
            }

            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val vCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pkg.longVersionCode
            } else {
                pkg.versionCode.toLong()
            }

            val icon = try {
                pm.getApplicationIcon(appInfo)
            } catch (e: Exception) {
                null
            }

            list.add(
                InstalledAppItem(
                    appName = appName,
                    packageName = pkg.packageName,
                    versionName = pkg.versionName ?: "1.0",
                    versionCode = vCode,
                    isSystemApp = isSystem,
                    lastUpdateTime = pkg.lastUpdateTime,
                    icon = icon
                )
            )
        }

        // Met en premier les applications utilisateur (non-système), triées par ordre alphabétique
        return list.sortedWith(
            compareBy<InstalledAppItem> { it.isSystemApp }
                .thenBy { it.appName.lowercase() }
        )
    }

    /**
     * Lance Google Play Store directement sur la page "Gérer les applications et l'appareil / Mises à jour",
     * où l'utilisateur peut appuyer en un seul clic sur "Tout mettre à jour".
     */
    fun launchGooglePlayUpdatesAll(context: Context) {
        val playStorePackage = "com.android.vending"

        // 1. Essai avec le lien direct vers le gestionnaire d'applications du Play Store
        val intentsToTry = listOf(
            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/apps")).apply {
                setPackage(playStorePackage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            Intent("com.google.android.finsky.VIEW_MY_DOWNLOADS").apply {
                setPackage(playStorePackage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:*")).apply {
                setPackage(playStorePackage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )

        var launched = false
        for (intent in intentsToTry) {
            try {
                context.startActivity(intent)
                launched = true
                break
            } catch (ignored: Exception) {
            }
        }

        if (!launched) {
            Toast.makeText(
                context,
                "Google Play Store introuvable. Ouverture du navigateur...",
                Toast.LENGTH_SHORT
            ).show()
            try {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps")
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(browserIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Impossible d'ouvrir le Play Store.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Ouvre la page Google Play Store pour une application installée spécifique afin de la mettre à jour.
     */
    fun launchAppUpdateInPlayStore(context: Context, packageName: String) {
        try {
            val marketIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            ).apply {
                setPackage("com.android.vending")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(marketIntent)
        } catch (e: ActivityNotFoundException) {
            try {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                Toast.makeText(
                    context,
                    "Impossible d'ouvrir la page de mise à jour pour $packageName",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Ouvre les paramètres système Android pour vérifier les mises à jour logicielles de l'appareil.
     */
    fun launchSystemUpdateSettings(context: Context) {
        val intents = listOf(
            Intent("android.settings.SYSTEM_UPDATE_SETTINGS"),
            Intent(Settings.ACTION_DEVICE_INFO_SETTINGS),
            Intent(Settings.ACTION_SETTINGS)
        )

        var opened = false
        for (intent in intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                opened = true
                break
            } catch (ignored: Exception) {
            }
        }

        if (!opened) {
            Toast.makeText(context, "Paramètres système inaccessibles.", Toast.LENGTH_SHORT).show()
        }
    }
}
