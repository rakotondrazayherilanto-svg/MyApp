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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.StudentEntity
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@Composable
fun StudentAttendanceDossierComponent(
    students: List<StudentEntity>,
    attendances: List<AttendanceEntity>,
    modifier: Modifier = Modifier
) {
    var selectedCycle by remember { mutableStateOf("Tous les Cycles") }
    val classes = remember(selectedCycle) {
        SchoolConstants.getClassesForCycle(selectedCycle)
    }
    var selectedClass by remember { mutableStateOf("Garderie") }

    val studentsInClass = remember(students, selectedClass) {
        students.filter { it.classe.equals(selectedClass, ignoreCase = true) }
    }

    var selectedStudentId by remember { mutableStateOf<Long?>(studentsInClass.firstOrNull()?.id) }

    // Make sure selectedStudentId is updated if class changes and current is not in class
    val currentStudent = students.firstOrNull { it.id == selectedStudentId } ?: studentsInClass.firstOrNull()

    val studentAttendances = remember(attendances, currentStudent) {
        if (currentStudent == null) emptyList()
        else attendances.filter { it.studentId == currentStudent.id }
    }

    val totalRecords = studentAttendances.size
    val presences = studentAttendances.count { it.statut == "Présent" }
    val absences = studentAttendances.count { it.statut == "Absent" }
    val retards = studentAttendances.count { it.statut == "Retard" }
    val justifies = studentAttendances.count { it.statut == "Justifié" }
    val rate = if (totalRecords > 0) (presences * 100) / totalRecords else 100

    LazyColumn(
        modifier = modifier.fillMaxWidth().testTag("student_dossier_list"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cycle & Class Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Cycle & Classe :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(SchoolConstants.CYCLES) { cycle ->
                        val isSelected = selectedCycle == cycle
                        Surface(
                            color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    selectedCycle = cycle
                                    val available = SchoolConstants.getClassesForCycle(cycle)
                                    if (selectedClass !in available) {
                                        selectedClass = available.first()
                                        val firstInCl = students.firstOrNull { it.classe.equals(selectedClass, ignoreCase = true) }
                                        selectedStudentId = firstInCl?.id
                                    }
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
                        val isSelected = selectedClass == cl
                        Surface(
                            color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    selectedClass = cl
                                    val firstInCl = students.firstOrNull { it.classe.equals(cl, ignoreCase = true) }
                                    selectedStudentId = firstInCl?.id
                                }
                        ) {
                            Text(
                                text = cl,
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

        // Student selector chips
        item {
            Column {
                Text(
                    text = "Choisir l'élève (${studentsInClass.size}) :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(studentsInClass) { st ->
                        val isSelected = currentStudent?.id == st.id
                        Surface(
                            color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedStudentId = st.id }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = st.nomComplet,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        if (currentStudent == null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Aucun élève sélectionné", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            // Student Summary Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = currentStudent.nomComplet,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Matricule : ${currentStudent.matricule} • Classe : ${currentStudent.classe}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFD1E4FF)
                                )
                            }
                            Surface(
                                color = GoldAccent,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "$rate% Assiduité",
                                    color = NavyPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress bar
                        LinearProgressIndicator(
                            progress = { rate / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = if (rate >= 85) EmeraldSuccess else if (rate >= 70) GoldAccent else CrimsonAlert,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            DossierStatItem("Séances", "$totalRecords", Color.White)
                            DossierStatItem("Présents", "$presences", EmeraldSuccess)
                            DossierStatItem("Absents", "$absences", CrimsonAlert)
                            DossierStatItem("Retards", "$retards", GoldAccent)
                            DossierStatItem("Justifiés", "$justifies", Color(0xFF93C5FD))
                        }
                    }
                }
            }

            // Attendance Timeline Title
            item {
                Text(
                    text = "Historique des Séances de ${currentStudent.prenoms} (${studentAttendances.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (studentAttendances.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Aucun pointage enregistré pour cet élève.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(studentAttendances) { att ->
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
                            Column {
                                Text(
                                    text = att.sessionMatiere,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Date : ${att.dateJour} • ${att.classe}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (att.motif.isNotBlank()) {
                                    Text(
                                        text = "Motif : ${att.motif}",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }

                            Surface(
                                color = statusColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = att.statut,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DossierStatItem(label: String, value: String, color: Color) {
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
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}
