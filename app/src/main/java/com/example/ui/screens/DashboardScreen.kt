package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.SchoolViewModel
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@Composable
fun DashboardScreen(
    viewModel: SchoolViewModel,
    onNavigateToSection: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.appSettings.collectAsState()
    val students by viewModel.students.collectAsState()
    val teachers by viewModel.teachers.collectAsState()
    val fees by viewModel.fees.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val attendances by viewModel.attendances.collectAsState()

    val totalRecettesAr = fees.sumOf { it.montantAr }
    val totalPresences = attendances.count { it.statut == "Présent" }
    val totalAbsences = attendances.count { it.statut == "Absent" }
    val presenceRate = if (attendances.isNotEmpty()) {
        ((totalPresences.toDouble() / attendances.size) * 100).toInt()
    } else 100

    var showSendDataDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero School Header with Banner
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card")
            ) {
                Box(modifier = Modifier.height(190.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.img_school_banner),
                        contentDescription = "${settings.schoolProfile.schoolAcronym} Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0x9907213A),
                                        Color(0xE607213A)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = GoldAccent,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = settings.schoolProfile.schoolAcronym,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = settings.schoolProfile.schoolName,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2
                        )
                        Text(
                            text = "${settings.schoolProfile.schoolMotto} • Année ${settings.schoolProfile.schoolYear}",
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Empty state announcement if brand new blank application
        if (students.isEmpty() && teachers.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("empty_state_starter_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = GoldAccent,
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Application vierge & prête !",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "Commencez dès maintenant à gérer votre établissement scolaire",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Vous pouvez inscrire vos élèves de la Garderie à la Terminale, enregistrer vos professeurs, configurer vos tarifs d'écolages ou adapter les couleurs et le nom de l'établissement.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onNavigateToSection(7) },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Inscrire Élève", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { onNavigateToSection(8) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Paramètres", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Data Transmission & Sync Card (Voluntary)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("send_data_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = GoldAccent,
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Sauvegarde vers la Direction",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Transmettez vos données au responsable (libre choix)",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { showSendDataDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Envoyer",
                            color = NavyPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Ariary Financial Highlight Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("financial_highlight_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Écolages & Recettes Totales",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.formatAriary(totalRecettesAr),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${fees.size} versements enregistrés",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Trésorerie Ariary",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 4 KPI Summary Cards in 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiStatCard(
                        title = "Élèves Inscrits",
                        value = "${students.size}",
                        subtitle = "5 classes actives",
                        icon = Icons.Default.School,
                        iconColor = NavyPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToSection(1) }
                    )
                    KpiStatCard(
                        title = "Enseignants",
                        value = "${teachers.size}",
                        subtitle = "Corps professoral",
                        icon = Icons.Default.Groups,
                        iconColor = GoldAccent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToSection(6) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiStatCard(
                        title = "Taux Présence",
                        value = "$presenceRate%",
                        subtitle = "$totalAbsences absences signalées",
                        icon = Icons.Default.CheckCircle,
                        iconColor = EmeraldSuccess,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToSection(5) }
                    )
                    KpiStatCard(
                        title = "Emploi du Temps",
                        value = "${schedules.size}",
                        subtitle = "Cours hebdomadaires",
                        icon = Icons.Default.CalendarMonth,
                        iconColor = Color(0xFF6366F1),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToSection(4) }
                    )
                }
            }
        }

        // Student Registration Banner (Entry Student)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSection(7) }
                    .testTag("entry_student_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = GoldAccent,
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = "Inscription",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Nouvelle Inscription Élève",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Enregistrer un élève dans la base locale Room",
                                color = Color(0xFFD1E4FF),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ouvrir",
                        tint = GoldAccent
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Text(
                text = "Accès Rapide",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    QuickActionButton(
                        icon = Icons.Default.PersonAdd,
                        label = "Inscrire Élève",
                        containerColor = NavyPrimary,
                        onClick = { onNavigateToSection(7) }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.Payment,
                        label = "Encaisser Écolage",
                        containerColor = GoldAccent,
                        onClick = { onNavigateToSection(2) }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.CheckCircle,
                        label = "Feuille d'Appel",
                        containerColor = EmeraldSuccess,
                        onClick = { onNavigateToSection(5) }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.Grade,
                        label = "Saisir Notes",
                        containerColor = Color(0xFF3B82F6),
                        onClick = { onNavigateToSection(3) }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.CalendarMonth,
                        label = "Emploi du Temps",
                        containerColor = Color(0xFF6366F1),
                        onClick = { onNavigateToSection(4) }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.MenuBook,
                        label = "Matières & Coefs",
                        containerColor = GoldAccent,
                        onClick = { onNavigateToSection(3) }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.Settings,
                        label = "Paramètres",
                        containerColor = Color(0xFF475569),
                        onClick = { onNavigateToSection(8) }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.SystemUpdate,
                        label = "Mises à jour",
                        containerColor = Color(0xFF0F172A),
                        onClick = { onNavigateToSection(9) }
                    )
                }
                item {
                    QuickActionButton(
                        icon = Icons.Default.Send,
                        label = "Transmettre",
                        containerColor = GoldAccent,
                        onClick = { showSendDataDialog = true }
                    )
                }
            }
        }

        // Recent Payments Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Derniers Versements (Ar)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Voir tout",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToSection(2) }
                )
            }
        }

        val recentFees = fees.take(4)
        if (recentFees.isEmpty()) {
            item {
                Text(
                    text = "Aucun versement enregistré.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(recentFees) { fee ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = EmeraldSuccess.copy(alpha = 0.15f),
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Payment,
                                        contentDescription = null,
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = fee.studentNom,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${fee.mois} • ${fee.classe} • ${fee.modePaiement}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = viewModel.formatAriary(fee.montantAr),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EmeraldSuccess
                            )
                            Text(
                                text = fee.datePaiement,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Today's course schedule sample
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cours & Emploi du Temps",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Consulter",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToSection(3) }
                )
            }
        }

        val topSchedules = schedules.take(3)
        items(topSchedules) { schedule ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = NavyPrimary,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = schedule.classe,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = schedule.matiere,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${schedule.enseignant} • ${schedule.salle}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${schedule.heureDebut} - ${schedule.heureFin}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NavyPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }

    if (showSendDataDialog) {
        SendDataToOwnerDialog(
            viewModel = viewModel,
            onDismiss = { showSendDataDialog = false }
        )
    }
}

@Composable
private fun KpiStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = iconColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
