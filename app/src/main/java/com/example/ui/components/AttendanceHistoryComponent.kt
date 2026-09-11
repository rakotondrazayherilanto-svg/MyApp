package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.data.AttendanceEntity
import com.example.data.SchoolConstants
import com.example.data.SubjectEntity
import com.example.ui.SchoolViewModel
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@Composable
fun AttendanceHistoryComponent(
    viewModel: SchoolViewModel,
    attendances: List<AttendanceEntity>,
    subjects: List<SubjectEntity>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterCycle by remember { mutableStateOf("Tous les Cycles") }
    var filterClass by remember { mutableStateOf("Toutes") }
    var filterSubject by remember { mutableStateOf("Toutes") }
    var filterStatus by remember { mutableStateOf("Tous") }

    var editingAttendance by remember { mutableStateOf<AttendanceEntity?>(null) }
    var editMotifText by remember { mutableStateOf("") }
    var editStatusText by remember { mutableStateOf("") }
    var attendanceToDelete by remember { mutableStateOf<AttendanceEntity?>(null) }

    val classes = remember(filterCycle) {
        listOf("Toutes") + SchoolConstants.getClassesForCycle(filterCycle)
    }
    val statuses = listOf("Tous", "Présent", "Absent", "Retard", "Justifié")

    val filteredList = remember(attendances, searchQuery, filterCycle, filterClass, filterSubject, filterStatus) {
        attendances.filter { att ->
            val matchQuery = searchQuery.isBlank() ||
                    att.studentNom.contains(searchQuery, ignoreCase = true) ||
                    att.studentMatricule.contains(searchQuery, ignoreCase = true) ||
                    att.dateJour.contains(searchQuery, ignoreCase = true)

            val matchCycle = filterCycle == "Tous les Cycles" || SchoolConstants.getCycleForClass(att.classe) == filterCycle
            val matchClass = filterClass == "Toutes" || SchoolConstants.classMatches(att.classe, filterClass)
            val matchSubject = filterSubject == "Toutes" || att.sessionMatiere.equals(filterSubject, ignoreCase = true)
            val matchStatus = filterStatus == "Tous" || att.statut.equals(filterStatus, ignoreCase = true)

            matchQuery && matchCycle && matchClass && matchSubject && matchStatus
        }
    }

    // Overall summary of filtered
    val totalFiltered = filteredList.size
    val presences = filteredList.count { it.statut == "Présent" }
    val absences = filteredList.count { it.statut == "Absent" }
    val retards = filteredList.count { it.statut == "Retard" }
    val justifies = filteredList.count { it.statut == "Justifié" }

    LazyColumn(
        modifier = modifier.fillMaxWidth().testTag("attendance_history_list"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Rechercher un élève, matricule ou date...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Effacer")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filtres & Registre d'Assiduité",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$totalFiltered pointages trouvés",
                            fontSize = 12.sp,
                            color = NavyPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        HistoryStatBadge("Présents", "$presences", EmeraldSuccess)
                        HistoryStatBadge("Absents", "$absences", CrimsonAlert)
                        HistoryStatBadge("Retards", "$retards", GoldAccent)
                        HistoryStatBadge("Justifiés", "$justifies", Color(0xFF2563EB))
                    }
                }
            }
        }

        // Cycle and Class Filters
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Filtrer par cycle & classe :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(SchoolConstants.CYCLES) { cycle ->
                        val isSelected = filterCycle == cycle
                        Surface(
                            color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    filterCycle = cycle
                                    filterClass = "Toutes"
                                }
                        ) {
                            Text(
                                text = cycle,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(classes) { cl ->
                        val isSelected = filterClass == cl
                        Surface(
                            color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { filterClass = cl }
                        ) {
                            Text(
                                text = cl,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Status Filters
        item {
            Column {
                Text(
                    text = "Filtrer par statut :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(statuses) { st ->
                        val isSelected = filterStatus == st
                        val chipColor = when (st) {
                            "Présent" -> EmeraldSuccess
                            "Absent" -> CrimsonAlert
                            "Retard" -> GoldAccent
                            "Justifié" -> Color(0xFF2563EB)
                            else -> NavyPrimary
                        }
                        Surface(
                            color = if (isSelected) chipColor else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { filterStatus = st }
                        ) {
                            Text(
                                text = st,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Subject Filters
        item {
            val allSubjectOptions = listOf("Toutes") + subjects.map { it.nom }
            Column {
                Text(
                    text = "Filtrer par matière :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(allSubjectOptions) { subj ->
                        val isSelected = filterSubject == subj
                        Surface(
                            color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { filterSubject = subj }
                        ) {
                            Text(
                                text = subj,
                                color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // List of attendance records
        if (filteredList.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Aucun enregistrement ne correspond aux critères.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(filteredList) { att ->
                val statusColor = when (att.statut) {
                    "Présent" -> EmeraldSuccess
                    "Absent" -> CrimsonAlert
                    "Retard" -> GoldAccent
                    else -> Color(0xFF2563EB)
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = att.studentNom,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${att.dateJour} • ${att.sessionMatiere} • ${att.classe} • ${att.studentMatricule}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (att.motif.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Motif : ${att.motif}",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = statusColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = att.statut,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    editingAttendance = att
                                    editMotifText = att.motif
                                    editStatusText = att.statut
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Modifier",
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(onClick = { attendanceToDelete = att }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Supprimer",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Attendance Dialog
    editingAttendance?.let { att ->
        val statusOptions = listOf("Présent", "Absent", "Retard", "Justifié")
        AlertDialog(
            onDismissRequest = { editingAttendance = null },
            title = { Text("Modifier le pointage", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Élève : ${att.studentNom}", fontWeight = FontWeight.SemiBold)
                    Text("Séance : ${att.sessionMatiere} (${att.dateJour})", fontSize = 12.sp)

                    Text("Statut :", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        statusOptions.forEach { st ->
                            val isSel = editStatusText == st
                            val color = when (st) {
                                "Présent" -> EmeraldSuccess
                                "Absent" -> CrimsonAlert
                                "Retard" -> GoldAccent
                                else -> Color(0xFF2563EB)
                            }
                            Surface(
                                color = if (isSel) color else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { editStatusText = st }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = st,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = editMotifText,
                        onValueChange = { editMotifText = it },
                        label = { Text("Motif ou justification") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateAttendanceRecord(
                            att.copy(
                                statut = editStatusText,
                                motif = editMotifText.trim()
                            )
                        )
                        editingAttendance = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingAttendance = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    attendanceToDelete?.let { att ->
        AlertDialog(
            onDismissRequest = { attendanceToDelete = null },
            title = { Text("Supprimer l'enregistrement") },
            text = { Text("Confirmer la suppression du pointage de ${att.studentNom} en ${att.sessionMatiere} le ${att.dateJour} ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAttendance(att)
                        attendanceToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { attendanceToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun HistoryStatBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
