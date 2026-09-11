package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.screens.ActivationLockScreen
import com.example.ui.screens.AppUpdatesScreen
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FeesScreen
import com.example.ui.screens.GradesScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.screens.SendDataToOwnerDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudentEntryScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.screens.TeachersScreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val licenseState by viewModel.licenseState.collectAsState()

    // VERROUILLAGE PRODUIT : Si l'application n'est pas activée avec une clé valide,
    // on bloque immédiatement l'accès et on affiche l'écran d'activation officiel.
    if (!licenseState.isActivated) {
        ActivationLockScreen(viewModel = viewModel, modifier = modifier)
        return
    }

    // 0: Accueil, 1: Élèves, 2: Écolages (Ar), 3: Notes & Matières, 4: Emploi du Temps, 5: Présences, 6: Enseignants, 7: Inscription Élève, 8: Paramètres, 9: Mises à jour
    var selectedSection by remember { mutableIntStateOf(0) }
    var menuExpanded by remember { mutableStateOf(false) }
    var showSendDataDialog by remember { mutableStateOf(false) }
    val settings by viewModel.appSettings.collectAsState()
    val acronym = settings.schoolProfile.schoolAcronym

    val screenTitles = listOf(
        "$acronym • Tableau de Bord",
        "$acronym • Registre des Élèves",
        "$acronym • Écolages & Finances (Ar)",
        "$acronym • Notes & Matières",
        "$acronym • Emploi du Temps",
        "$acronym • Feuilles de Présences",
        "$acronym • Corps Enseignant",
        "$acronym • Inscription Élève",
        "$acronym • Paramètres & Personnalisation",
        "$acronym • Mises à jour des Applications"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_lp3f_icon),
                            contentDescription = "$acronym Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = screenTitles.getOrElse(selectedSection) { acronym },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    // Quick button to open Student Entry (Inscription)
                    IconButton(onClick = { selectedSection = 7 }) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Inscrire Élève",
                            tint = if (selectedSection == 7) GoldAccent else Color.White
                        )
                    }
                    // Quick button to jump to Enseignants or Présences
                    IconButton(onClick = { selectedSection = 5 }) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Présences",
                            tint = if (selectedSection == 5) GoldAccent else Color.White
                        )
                    }
                    IconButton(onClick = { selectedSection = 6 }) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Enseignants",
                            tint = if (selectedSection == 6) GoldAccent else Color.White
                        )
                    }
                    // Quick button to Settings
                    IconButton(onClick = { selectedSection = 8 }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Paramètres & Personnalisation",
                            tint = if (selectedSection == 8) GoldAccent else Color.White
                        )
                    }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tableau de Bord") },
                            leadingIcon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                            onClick = {
                                selectedSection = 0
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Inscrire un Élève") },
                            leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                            onClick = {
                                selectedSection = 7
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Registre des Élèves") },
                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                            onClick = {
                                selectedSection = 1
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Écolages & Frais (Ar)") },
                            leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                            onClick = {
                                selectedSection = 2
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Notes & Matières") },
                            leadingIcon = { Icon(Icons.Default.Grade, contentDescription = null) },
                            onClick = {
                                selectedSection = 3
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Emploi du Temps") },
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                            onClick = {
                                selectedSection = 4
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Présences & Assiduité") },
                            leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                            onClick = {
                                selectedSection = 5
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Enseignants") },
                            leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null) },
                            onClick = {
                                selectedSection = 6
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Paramètres & Personnalisation") },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            onClick = {
                                selectedSection = 8
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mises à jour des Applications") },
                            leadingIcon = { Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = GoldAccent) },
                            onClick = {
                                selectedSection = 9
                                menuExpanded = false
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Transmettre Données (Direction)") },
                            leadingIcon = { Icon(Icons.Default.Send, contentDescription = null, tint = GoldAccent) },
                            onClick = {
                                menuExpanded = false
                                showSendDataDialog = true
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Accueil") },
                    label = { Text("Accueil", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = NavyPrimary,
                        selectedTextColor = NavyPrimary
                    )
                )
                NavigationBarItem(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    icon = { Icon(Icons.Default.School, contentDescription = "Élèves") },
                    label = { Text("Élèves", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = NavyPrimary,
                        selectedTextColor = NavyPrimary
                    )
                )
                NavigationBarItem(
                    selected = selectedSection == 2,
                    onClick = { selectedSection = 2 },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Écolages") },
                    label = { Text("Écolages", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = GoldAccent.copy(alpha = 0.25f),
                        selectedIconColor = GoldAccent,
                        selectedTextColor = GoldAccent
                    )
                )
                NavigationBarItem(
                    selected = selectedSection == 3,
                    onClick = { selectedSection = 3 },
                    icon = { Icon(Icons.Default.Grade, contentDescription = "Notes") },
                    label = { Text("Notes", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = NavyPrimary,
                        selectedTextColor = NavyPrimary
                    )
                )
                NavigationBarItem(
                    selected = selectedSection == 4,
                    onClick = { selectedSection = 4 },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Horaires") },
                    label = { Text("Horaires", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = NavyPrimary,
                        selectedTextColor = NavyPrimary
                    )
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedSection,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.padding(innerPadding),
            label = "ScreenTransition"
        ) { targetSection ->
            when (targetSection) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToSection = { newSection -> selectedSection = newSection }
                )
                1 -> StudentsScreen(viewModel = viewModel)
                2 -> FeesScreen(viewModel = viewModel)
                3 -> GradesScreen(viewModel = viewModel)
                4 -> ScheduleScreen(viewModel = viewModel)
                5 -> AttendanceScreen(viewModel = viewModel)
                6 -> TeachersScreen(viewModel = viewModel)
                7 -> StudentEntryScreen(
                    viewModel = viewModel,
                    onStudentCreated = { selectedSection = 1 }
                )
                8 -> SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { selectedSection = 0 },
                    onNavigateToUpdates = { selectedSection = 9 }
                )
                9 -> AppUpdatesScreen(
                    viewModel = viewModel,
                    onNavigateBack = { selectedSection = 0 }
                )
                else -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToSection = { newSection -> selectedSection = newSection }
                )
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
