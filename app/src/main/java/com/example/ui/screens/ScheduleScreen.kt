package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ScheduleEntity
import com.example.data.SchoolConstants
import com.example.ui.SchoolViewModel
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val schedules by viewModel.schedules.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val teachers by viewModel.teachers.collectAsState()

    val daysOfWeek = listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi")
    var selectedDay by remember { mutableStateOf("Lundi") }

    var selectedCycleFilter by remember { mutableStateOf("Tous les Cycles") }
    var selectedClass by remember { mutableStateOf("Toutes") }

    val classes = remember(selectedCycleFilter) {
        listOf("Toutes") + SchoolConstants.getClassesForCycle(selectedCycleFilter)
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var scheduleToDelete by remember { mutableStateOf<ScheduleEntity?>(null) }

    val filteredSchedules = schedules.filter { s ->
        s.jourSemaine.equals(selectedDay, ignoreCase = true) &&
        (selectedCycleFilter == "Tous les Cycles" || SchoolConstants.getCycleForClass(s.classe) == selectedCycleFilter) &&
        (selectedClass == "Toutes" || SchoolConstants.classMatches(s.classe, selectedClass))
    }.sortedBy { it.heureDebut }

    Box(modifier = modifier.fillMaxSize().testTag("schedule_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Emploi du Temps Hebdomadaire",
                            color = Color(0xFFD1E4FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Planning des Cours LP3F",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lycée Privée FJKM Fenoarivobe Fahasoavana",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Days filter chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(daysOfWeek) { day ->
                        val isSelected = selectedDay == day
                        Surface(
                            color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedDay = day }
                        ) {
                            Text(
                                text = day,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // Cycles and Classes filter chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(SchoolConstants.CYCLES) { cycle ->
                            val isSelected = selectedCycleFilter == cycle
                            Surface(
                                color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        selectedCycleFilter = cycle
                                        selectedClass = "Toutes"
                                    }
                            ) {
                                Text(
                                    text = cycle,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(classes) { cl ->
                            val isSelected = selectedClass == cl
                            Surface(
                                color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { selectedClass = cl }
                            ) {
                                Text(
                                    text = cl,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (filteredSchedules.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(36.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Aucun cours programmé pour $selectedDay (${selectedClass})",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredSchedules) { item ->
                    ScheduleCard(
                        schedule = item,
                        onDelete = { scheduleToDelete = item }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }

        // Add Schedule FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = NavyPrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_schedule_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ajouter Cours", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Add Schedule Dialog
    if (showAddDialog) {
        AddScheduleDialog(
            subjects = subjects.map { it.nom },
            teachers = teachers.map { it.nomComplet },
            initialDay = selectedDay,
            onDismiss = { showAddDialog = false },
            onConfirm = { cl, day, debut, fin, mat, prof, salle ->
                viewModel.addSchedule(cl, day, debut, fin, mat, prof, salle)
                showAddDialog = false
            }
        )
    }

    // Delete Schedule Confirmation
    scheduleToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { scheduleToDelete = null },
            title = { Text("Supprimer le créneau") },
            text = { Text("Supprimer le cours de ${item.matiere} (${item.classe}, ${item.jourSemaine} ${item.heureDebut}-${item.heureFin}) ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSchedule(item)
                        scheduleToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { scheduleToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun ScheduleCard(
    schedule: ScheduleEntity,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time block
            Surface(
                color = NavyPrimary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(86.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = schedule.heureDebut,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = NavyPrimary
                    )
                    Text(
                        text = schedule.heureFin,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = GoldAccent,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = schedule.classe,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = schedule.matiere,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = schedule.enseignant,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = schedule.salle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer cours",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddScheduleDialog(
    subjects: List<String>,
    teachers: List<String>,
    initialDay: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, String) -> Unit
) {
    var selectedCycle by remember { mutableStateOf("Lycée") }
    var expandedCycle by remember { mutableStateOf(false) }

    val classes = remember(selectedCycle) {
        SchoolConstants.getClassesForCycle(selectedCycle)
    }
    var selectedClasse by remember { mutableStateOf("2nde") }
    var expandedClasse by remember { mutableStateOf(false) }

    val days = listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi")
    var selectedDay by remember { mutableStateOf(initialDay) }
    var expandedDay by remember { mutableStateOf(false) }

    var heureDebut by remember { mutableStateOf("07:30") }
    var heureFin by remember { mutableStateOf("09:30") }

    var matiere by remember { mutableStateOf(subjects.firstOrNull() ?: "Mathématiques") }
    var expandedMatiere by remember { mutableStateOf(false) }

    var enseignant by remember { mutableStateOf(teachers.firstOrNull() ?: "M. Rakoto") }
    var salle by remember { mutableStateOf("Salle 01") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouveau Créneau de Cours", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    // Cycle selector
                    ExposedDropdownMenuBox(
                        expanded = expandedCycle,
                        onExpandedChange = { expandedCycle = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCycle,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Cycle") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCycle) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCycle,
                            onDismissRequest = { expandedCycle = false }
                        ) {
                            SchoolConstants.CYCLES.filter { it != "Tous les Cycles" }.forEach { cy ->
                                DropdownMenuItem(
                                    text = { Text(cy) },
                                    onClick = {
                                        selectedCycle = cy
                                        selectedClasse = SchoolConstants.getClassesForCycle(cy).first()
                                        expandedCycle = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Classe
                    ExposedDropdownMenuBox(
                        expanded = expandedClasse,
                        onExpandedChange = { expandedClasse = it }
                    ) {
                        OutlinedTextField(
                            value = selectedClasse,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Classe") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedClasse) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedClasse,
                            onDismissRequest = { expandedClasse = false }
                        ) {
                            classes.forEach { cl ->
                                DropdownMenuItem(
                                    text = { Text(cl) },
                                    onClick = {
                                        selectedClasse = cl
                                        expandedClasse = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Jour
                    ExposedDropdownMenuBox(
                        expanded = expandedDay,
                        onExpandedChange = { expandedDay = it }
                    ) {
                        OutlinedTextField(
                            value = selectedDay,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jour") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDay) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDay,
                            onDismissRequest = { expandedDay = false }
                        ) {
                            days.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d) },
                                    onClick = {
                                        selectedDay = d
                                        expandedDay = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = heureDebut,
                            onValueChange = { heureDebut = it },
                            label = { Text("Début (ex: 07:30)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = heureFin,
                            onValueChange = { heureFin = it },
                            label = { Text("Fin (ex: 09:30)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    if (subjects.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = expandedMatiere,
                            onExpandedChange = { expandedMatiere = it }
                        ) {
                            OutlinedTextField(
                                value = matiere,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Matière") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMatiere) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedMatiere,
                                onDismissRequest = { expandedMatiere = false }
                            ) {
                                subjects.forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text(m) },
                                        onClick = {
                                            matiere = m
                                            expandedMatiere = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = matiere,
                            onValueChange = { matiere = it },
                            label = { Text("Matière") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = enseignant,
                        onValueChange = { enseignant = it },
                        label = { Text("Enseignant") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = salle,
                        onValueChange = { salle = it },
                        label = { Text("Salle de classe") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (heureDebut.isNotBlank() && heureFin.isNotBlank() && matiere.isNotBlank()) {
                        onConfirm(selectedClasse, selectedDay, heureDebut, heureFin, matiere, enseignant, salle)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Programmer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
