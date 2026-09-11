package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppColorTheme
import com.example.data.FontScale
import com.example.data.SchoolProfile
import com.example.data.ThemeMode
import com.example.ui.SchoolViewModel
import com.example.ui.screens.OwnerKeyGeneratorDialog
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary
import com.example.utils.AppUpdateManager

@Composable
fun SettingsScreen(
    viewModel: SchoolViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToUpdates: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.appSettings.collectAsState()
    val licenseState by viewModel.licenseState.collectAsState()

    val students by viewModel.students.collectAsState()
    val teachers by viewModel.teachers.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val fees by viewModel.fees.collectAsState()
    val grades by viewModel.grades.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val attendances by viewModel.attendances.collectAsState()

    var showClearDataDialog by remember { mutableStateOf(false) }
    var showReloadDemoDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showOwnerKeyGenDialog by remember { mutableStateOf(false) }
    var showDeactivateLicenseDialog by remember { mutableStateOf(false) }
    var showSendDataDialog by remember { mutableStateOf(false) }
    var keepSubjectsChecked by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. School Identity Card Header
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = settings.schoolProfile.schoolAcronym,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Année ${settings.schoolProfile.schoolYear}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        IconButton(onClick = { showEditProfileDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Modifier profil",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = settings.schoolProfile.schoolName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "« ${settings.schoolProfile.schoolMotto} »",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    Text(
                        text = "📍 ${settings.schoolProfile.schoolAddress}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // 2. Display & Appearance Settings Section
        item {
            Text(
                text = "AFFICHAGE ET APPARENCE",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
        }

        // Mode Sombre / Clair / Système
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BrightnessAuto,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Mode d'affichage",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionButton(
                            title = "Système",
                            icon = Icons.Default.BrightnessAuto,
                            selected = settings.themeMode == ThemeMode.SYSTEM,
                            onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionButton(
                            title = "Clair",
                            icon = Icons.Default.LightMode,
                            selected = settings.themeMode == ThemeMode.LIGHT,
                            onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionButton(
                            title = "Sombre",
                            icon = Icons.Default.DarkMode,
                            selected = settings.themeMode == ThemeMode.DARK,
                            onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Color Themes
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Palette de couleurs de l'application",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ColorThemeRow(
                            name = "Bleu Marine & Or (LP3F Traditionnel)",
                            primary = Color(0xFF0D3B66),
                            secondary = Color(0xFFD97706),
                            isSelected = settings.colorTheme == AppColorTheme.NAVY_GOLD,
                            onClick = { viewModel.setColorTheme(AppColorTheme.NAVY_GOLD) }
                        )
                        ColorThemeRow(
                            name = "Vert Émeraude & Or (Nature & Espoir)",
                            primary = Color(0xFF047857),
                            secondary = Color(0xFFD97706),
                            isSelected = settings.colorTheme == AppColorTheme.EMERALD,
                            onClick = { viewModel.setColorTheme(AppColorTheme.EMERALD) }
                        )
                        ColorThemeRow(
                            name = "Bleu Roi & Argent (Moderne & Académique)",
                            primary = Color(0xFF1D4ED8),
                            secondary = Color(0xFF0284C7),
                            isSelected = settings.colorTheme == AppColorTheme.ROYAL_BLUE,
                            onClick = { viewModel.setColorTheme(AppColorTheme.ROYAL_BLUE) }
                        )
                        ColorThemeRow(
                            name = "Violet Impérial & Ambre (Excellence)",
                            primary = Color(0xFF6B21A8),
                            secondary = Color(0xFFF59E0B),
                            isSelected = settings.colorTheme == AppColorTheme.IMPERIAL_PURPLE,
                            onClick = { viewModel.setColorTheme(AppColorTheme.IMPERIAL_PURPLE) }
                        )
                        ColorThemeRow(
                            name = "Bordeaux & Cuivre (Prestige & Chaleur)",
                            primary = Color(0xFF881337),
                            secondary = Color(0xFFEA580C),
                            isSelected = settings.colorTheme == AppColorTheme.BURGUNDY,
                            onClick = { viewModel.setColorTheme(AppColorTheme.BURGUNDY) }
                        )
                    }
                }
            }
        }

        // Font Scale (Taille du texte)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Taille du texte / Lisibilité",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FontScaleOptionButton(
                            title = "Compact",
                            description = "90%",
                            selected = settings.fontScale == FontScale.COMPACT,
                            onClick = { viewModel.setFontScale(FontScale.COMPACT) },
                            modifier = Modifier.weight(1f)
                        )
                        FontScaleOptionButton(
                            title = "Standard",
                            description = "100%",
                            selected = settings.fontScale == FontScale.NORMAL,
                            onClick = { viewModel.setFontScale(FontScale.NORMAL) },
                            modifier = Modifier.weight(1f)
                        )
                        FontScaleOptionButton(
                            title = "Grand",
                            description = "115%",
                            selected = settings.fontScale == FontScale.LARGE,
                            onClick = { viewModel.setFontScale(FontScale.LARGE) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. School Profile Settings Button
        item {
            Text(
                text = "PERSONNALISATION DE L'ÉTABLISSEMENT",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Coordonnées et en-têtes officiels",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Ces informations sont utilisées sur l'écran d'accueil ainsi que sur tous les bulletins de notes et reçus de paiement d'écolages exportés en PDF pour les parents.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Modifier les informations de l'école")
                    }
                }
            }
        }

        // 4. Data Management & Reset to Blank Slate
        item {
            Text(
                text = "GESTION DES DONNÉES & ÉTAT VIERGE",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = CrimsonAlert,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "État actuel de la base de données",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Stats grid
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• Élèves inscrits :", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${students.size}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• Enseignants enregistrés :", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${teachers.size}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• Matières pédagogiques :", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${subjects.size}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• Versements d'écolages enregistrés :", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${fees.size}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• Notes saisies :", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${grades.size}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• Cours à l'emploi du temps :", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${schedules.size}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• Feuilles de présence :", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${attendances.size}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Primary Reset Button (Vider l'application pour démarrer à blanc)
                    Button(
                        onClick = { showClearDataDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CrimsonAlert,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Vider l'application (Démarrer à blanc)",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Permet d'effacer toutes les données d'essai pour commencer à ajouter vos propres élèves, enseignants, cours et écolages sur une base 100% vierge.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Secondary Reload Demo Button
                    OutlinedButton(
                        onClick = { showReloadDemoDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Recharger les exemples de démonstration")
                    }
                }
            }
        }

        // 5. Product License & Security
        item {
            Text(
                text = "SÉCURITÉ & CLÉ DE PRODUIT",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "État de la Licence : Activée",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EmeraldSuccess
                            )
                        }

                        Surface(
                            color = EmeraldSuccess.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "OFFICIELLE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldSuccess,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• ID Appareil :", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(licenseState.installationId, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• Clé Enregistrée :", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(licenseState.productKey.ifBlank { "Validée" }, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showOwnerKeyGenDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Générer des Clés", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showDeactivateLicenseDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAlert),
                            border = BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verrouiller", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 6. Application Updates Manager
        item {
            Text(
                text = "MISES À JOUR DES APPLICATIONS",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gestionnaire de Mises à Jour de l'Appareil",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = "Lancez en un seul clic la mise à jour de l'ensemble des applications installées sur votre smartphone ou consultez la liste détaillée de chaque application.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { AppUpdateManager.launchGooglePlayUpdatesAll(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tout Mettre à Jour", color = NavyPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = onNavigateToUpdates,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Voir Détails", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // 6.5. Transmission & Sauvegarde des Données au Responsable
        item {
            Text(
                text = "TRANSMISSION & SAUVEGARDE AU RESPONSABLE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = NavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Envoi des Données au Propriétaire",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "FACULTATIF",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1D4ED8),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Vous pouvez transmettre directement les informations enregistrées (élèves, écolages Ar, notes, professeurs) à l'adresse de la direction : ${settings.schoolProfile.ownerEmail}. L'envoi est entièrement facultatif selon votre décision.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { showSendDataDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Transmettre mes Données", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 7. System Info & Security
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Stockage 100% Hors-Ligne & Local",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = EmeraldSuccess
                        )
                    }
                    Text(
                        text = "Toutes les données de votre établissement sont conservées directement sur votre appareil Android en toute sécurité, sans connexion internet obligatoire.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ==========================================
    // DIALOG: OWNER KEY GENERATOR
    // ==========================================
    if (showOwnerKeyGenDialog) {
        OwnerKeyGeneratorDialog(
            currentInstallationId = licenseState.installationId,
            viewModel = viewModel,
            onDismiss = { showOwnerKeyGenDialog = false },
            onAutoActivate = { key ->
                viewModel.activateProduct(key)
                showOwnerKeyGenDialog = false
                Toast.makeText(context, "Licence mise à jour !", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // ==========================================
    // DIALOG: DEACTIVATE / LOCK APP
    // ==========================================
    if (showDeactivateLicenseDialog) {
        AlertDialog(
            onDismissRequest = { showDeactivateLicenseDialog = false },
            icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CrimsonAlert) },
            title = { Text("Verrouiller l'application ?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Cette action va révoquer la clé de produit active sur cet appareil. L'application se verrouillera immédiatement et exigera à nouveau une clé de produit valide pour être ouverte.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deactivateProduct()
                        showDeactivateLicenseDialog = false
                        Toast.makeText(context, "Application verrouillée avec succès.", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert)
                ) {
                    Text("Oui, Verrouiller")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeactivateLicenseDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // ==========================================
    // DIALOG: EDIT SCHOOL PROFILE
    // ==========================================
    if (showEditProfileDialog) {
        var nom by remember { mutableStateOf(settings.schoolProfile.schoolName) }
        var sigle by remember { mutableStateOf(settings.schoolProfile.schoolAcronym) }
        var devise by remember { mutableStateOf(settings.schoolProfile.schoolMotto) }
        var adresse by remember { mutableStateOf(settings.schoolProfile.schoolAddress) }
        var tel by remember { mutableStateOf(settings.schoolProfile.schoolPhone) }
        var email by remember { mutableStateOf(settings.schoolProfile.schoolEmail) }
        var annee by remember { mutableStateOf(settings.schoolProfile.schoolYear) }
        var proviseur by remember { mutableStateOf(settings.schoolProfile.principalTitle) }
        var comptable by remember { mutableStateOf(settings.schoolProfile.cashierTitle) }
        var ownerEmail by remember { mutableStateOf(settings.schoolProfile.ownerEmail) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = "Personnaliser l'établissement",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = nom,
                            onValueChange = { nom = it },
                            label = { Text("Nom complet de l'établissement") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = sigle,
                                onValueChange = { sigle = it },
                                label = { Text("Sigle (ex: LP3F)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = annee,
                                onValueChange = { annee = it },
                                label = { Text("Année scolaire") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = devise,
                            onValueChange = { devise = it },
                            label = { Text("Devise / Slogan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = adresse,
                            onValueChange = { adresse = it },
                            label = { Text("Adresse / Ville / Région") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = tel,
                                onValueChange = { tel = it },
                                label = { Text("Téléphone") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = proviseur,
                            onValueChange = { proviseur = it },
                            label = { Text("Titre du Chef d'établissement (Bulletins)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = comptable,
                            onValueChange = { comptable = it },
                            label = { Text("Titre du Responsable Caisse (Reçus)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = ownerEmail,
                            onValueChange = { ownerEmail = it },
                            label = { Text("Email du Propriétaire / Destinataire des données") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateSchoolProfile(
                            SchoolProfile(
                                schoolName = nom.trim().ifEmpty { "Lycée Privé FJKM Fenoarivobe Fahasoavana" },
                                schoolAcronym = sigle.trim().ifEmpty { "LP3F" },
                                schoolMotto = devise.trim(),
                                schoolAddress = adresse.trim(),
                                schoolPhone = tel.trim(),
                                schoolEmail = email.trim(),
                                schoolYear = annee.trim().ifEmpty { "2026 - 2027" },
                                principalTitle = proviseur.trim().ifEmpty { "Le Chef d'Établissement (LP3F)" },
                                cashierTitle = comptable.trim().ifEmpty { "Le Service Comptabilité (LP3F)" },
                                ownerEmail = ownerEmail.trim().ifEmpty { "rakotondrazayherilanto@gmail.com" }
                            )
                        )
                        showEditProfileDialog = false
                        Toast.makeText(context, "Profil de l'établissement mis à jour !", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // ==========================================
    // DIALOG: CLEAR ALL DATA (REMISE À BLANC)
    // ==========================================
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = CrimsonAlert,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Vider toutes les données ?",
                    fontWeight = FontWeight.Bold,
                    color = CrimsonAlert
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Cette action va réinitialiser l'application pour la rendre totalement vierge. Vous pourrez ensuite commencer à saisir vos propres élèves, enseignants, écolages, notes et emplois du temps.",
                        fontSize = 13.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { keepSubjectsChecked = !keepSubjectsChecked }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = keepSubjectsChecked,
                            onCheckedChange = { keepSubjectsChecked = it }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Conserver la liste des matières scolaires (Malagasy, Maths, Français... Recommandé)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData(keepSubjects = keepSubjectsChecked) {
                            Toast.makeText(
                                context,
                                "Application remise à zéro ! Vous pouvez démarrer vos saisies.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CrimsonAlert,
                        contentColor = Color.White
                    )
                ) {
                    Text("Confirmer et vider")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearDataDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // ==========================================
    // DIALOG: RELOAD DEMO DATA
    // ==========================================
    if (showReloadDemoDialog) {
        AlertDialog(
            onDismissRequest = { showReloadDemoDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Charger les données de démonstration ?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Cette action chargera des exemples d'élèves, enseignants, paiements et plannings pour illustrer le fonctionnement de l'application.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reloadDemoData {
                            Toast.makeText(
                                context,
                                "Données de démonstration chargées avec succès !",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        showReloadDemoDialog = false
                    }
                ) {
                    Text("Charger")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReloadDemoDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    if (showSendDataDialog) {
        SendDataToOwnerDialog(
            viewModel = viewModel,
            onDismiss = { showSendDataDialog = false }
        )
    }
}

// ==========================================
// COMPOSANTS AUXILIAIRES
// ==========================================

@Composable
private fun ThemeOptionButton(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = modifier
            .height(68.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ColorThemeRow(
    name: String,
    primary: Color,
    secondary: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else Color.Transparent,
        border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Color dots preview
                Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(primary)
                            .border(1.dp, Color.White, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(secondary)
                            .border(1.dp, Color.White, CircleShape)
                    )
                }

                Text(
                    text = name,
                    fontSize = 12.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Sélectionné",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun FontScaleOptionButton(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = modifier
            .height(60.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = description,
                fontSize = 10.sp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
