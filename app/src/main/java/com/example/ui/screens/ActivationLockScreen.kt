package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SchoolViewModel
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@Composable
fun ActivationLockScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val licenseState by viewModel.licenseState.collectAsState()
    val settings by viewModel.appSettings.collectAsState()

    val installationId = licenseState.installationId.ifBlank { viewModel.getInstallationId() }

    var enteredKey by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showOwnerModal by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Dark slate
                        Color(0xFF1E293B),
                        NavyPrimary
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Security Badge Icon
            Surface(
                color = GoldAccent.copy(alpha = 0.15f),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(2.dp, GoldAccent),
                modifier = Modifier.size(90.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Protection par Clé",
                        tint = GoldAccent,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Header Titles
            Text(
                text = settings.schoolProfile.schoolAcronym,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Activation Requise",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Cette application est protégée. Vous devez disposer d'une clé de produit officielle fournie par le propriétaire pour y accéder.",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Installation ID Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.9f)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ID d'Installation de l'appareil",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFCBD5E1)
                            )
                        }

                        // Copy button
                        OutlinedButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(installationId))
                                Toast.makeText(context, "ID d'installation copié !", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f)),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copier",
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Copier",
                                fontSize = 11.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Big Monospace ID display
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = installationId,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Transmettez cet identifiant au propriétaire pour recevoir votre clé de produit personnalisée.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product Key Input Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Saisir la Clé de Produit",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = enteredKey,
                        onValueChange = {
                            enteredKey = it.uppercase()
                            errorMessage = null
                        },
                        placeholder = {
                            Text(
                                text = "LP3F-XXXX-XXXX-XXXX",
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = GoldAccent
                            )
                        },
                        trailingIcon = {
                            if (enteredKey.isNotBlank()) {
                                IconButton(onClick = { enteredKey = "" }) {
                                    Text("✕", color = Color.Gray, fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (enteredKey.isNotBlank()) {
                                    val success = viewModel.activateProduct(enteredKey)
                                    if (!success) {
                                        errorMessage = "Clé de produit non valide pour cet appareil. Veuillez contacter le propriétaire."
                                    }
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("product_key_input_field")
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFF87171),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (enteredKey.isBlank()) {
                                errorMessage = "Veuillez saisir votre clé de produit."
                                return@Button
                            }
                            val success = viewModel.activateProduct(enteredKey)
                            if (success) {
                                Toast.makeText(context, "Application activée avec succès !", Toast.LENGTH_LONG).show()
                            } else {
                                errorMessage = "Clé de produit incorrecte pour l'ID $installationId."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("validate_license_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Valider & Déverrouiller l'Application",
                            color = NavyPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Owner section trigger
            OutlinedButton(
                onClick = { showOwnerModal = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFCBD5E1)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_owner_generator_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Accès Propriétaire (Fournir / Générer des Clés)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Modal Espace Propriétaire / Générateur de Clés
    if (showOwnerModal) {
        OwnerKeyGeneratorDialog(
            currentInstallationId = installationId,
            viewModel = viewModel,
            onDismiss = { showOwnerModal = false },
            onAutoActivate = { key ->
                viewModel.activateProduct(key)
                showOwnerModal = false
                Toast.makeText(context, "Application activée avec succès par le propriétaire !", Toast.LENGTH_LONG).show()
            }
        )
    }
}

@Composable
fun OwnerKeyGeneratorDialog(
    currentInstallationId: String,
    viewModel: SchoolViewModel,
    onDismiss: () -> Unit,
    onAutoActivate: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var isOwnerAuthenticated by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    // Target Installation ID for which key should be generated
    var targetIdInput by remember { mutableStateOf(currentInstallationId) }
    var generatedKey by remember { mutableStateOf(viewModel.generateKeyForInstallationId(currentInstallationId)) }

    // State pour modifier le code PIN Propriétaire
    var showChangePinSection by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var changePinSuccessMessage by remember { mutableStateOf<String?>(null) }
    var changePinErrorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (isOwnerAuthenticated) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Fermer")
                }
            } else {
                Button(
                    onClick = {
                        if (viewModel.verifyOwnerPin(pinInput)) {
                            isOwnerAuthenticated = true
                            pinError = null
                        } else {
                            pinError = "Code PIN incorrect (Code par défaut : 2026)"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("Déverrouiller Espace", color = NavyPrimary, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isOwnerAuthenticated) "Générateur de Clés de Produit" else "Authentification Propriétaire",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!isOwnerAuthenticated) {
                    Text(
                        text = "Veuillez entrer votre Code PIN Propriétaire pour accéder au générateur et fournir des clés d'activation.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            pinInput = it
                            pinError = null
                        },
                        label = { Text("Code PIN Propriétaire") },
                        placeholder = { Text("ex: 2026") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (pinError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = pinError ?: "",
                            color = Color.Red,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Note : Le code par défaut est 2026.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                } else {
                    // Espace Générateur pour le propriétaire
                    Text(
                        text = "En tant que propriétaire, vous seul avez le pouvoir de générer et de fournir les clés de produit pour vos appareils.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "ID d'Installation de l'appareil client :",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = targetIdInput,
                        onValueChange = {
                            targetIdInput = it.uppercase()
                            generatedKey = viewModel.generateKeyForInstallationId(targetIdInput)
                        },
                        singleLine = true,
                        placeholder = { Text("LP3F-XXXX-XXXX") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Clé de Produit Officielle Générée :",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = generatedKey,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NavyPrimary
                            )

                            Row {
                                IconButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(generatedKey))
                                        Toast.makeText(context, "Clé copiée !", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copier la clé",
                                        tint = NavyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        val shareText = "Voici votre clé de produit officielle LP3F pour l'appareil ($targetIdInput) :\n\nClé : $generatedKey\n\nFournie avec la permission de la Direction."
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Partager la Clé de Produit"))
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Partager",
                                        tint = GoldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Si l'ID correspond à l'appareil en cours, bouton d'activation en 1 clic
                    if (targetIdInput.equals(currentInstallationId, ignoreCase = true)) {
                        Button(
                            onClick = { onAutoActivate(generatedKey) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Activer immédiatement cet appareil", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Section pour modifier le code PIN Propriétaire
                    OutlinedButton(
                        onClick = {
                            showChangePinSection = !showChangePinSection
                            changePinSuccessMessage = null
                            changePinErrorMessage = null
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Password, contentDescription = null, modifier = Modifier.size(16.dp), tint = NavyPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showChangePinSection) "Masquer changement de PIN" else "Modifier mon Code PIN Propriétaire",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (showChangePinSection) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Définir un nouveau code PIN secret :",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = newPinInput,
                                    onValueChange = {
                                        newPinInput = it
                                        changePinErrorMessage = null
                                        changePinSuccessMessage = null
                                    },
                                    label = { Text("Nouveau Code PIN") },
                                    placeholder = { Text("ex: 1234 ou votre code") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = confirmPinInput,
                                    onValueChange = {
                                        confirmPinInput = it
                                        changePinErrorMessage = null
                                        changePinSuccessMessage = null
                                    },
                                    label = { Text("Confirmer le Nouveau Code PIN") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (changePinErrorMessage != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = changePinErrorMessage ?: "",
                                        color = Color.Red,
                                        fontSize = 11.sp
                                    )
                                }

                                if (changePinSuccessMessage != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = changePinSuccessMessage ?: "",
                                        color = EmeraldSuccess,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        val cleanNewPin = newPinInput.trim()
                                        val cleanConfirm = confirmPinInput.trim()

                                        if (cleanNewPin.length < 4) {
                                            changePinErrorMessage = "Le code PIN doit comporter au moins 4 caractères ou chiffres."
                                        } else if (cleanNewPin != cleanConfirm) {
                                            changePinErrorMessage = "Les deux codes saisis ne correspondent pas."
                                        } else {
                                            viewModel.setOwnerPin(cleanNewPin)
                                            changePinSuccessMessage = "Code PIN modifié avec succès !"
                                            changePinErrorMessage = null
                                            newPinInput = ""
                                            confirmPinInput = ""
                                            Toast.makeText(context, "Code PIN Propriétaire mis à jour !", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Enregistrer le nouveau PIN", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
