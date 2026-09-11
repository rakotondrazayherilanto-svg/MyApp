package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AttendanceEntity
import com.example.data.SchoolConstants
import com.example.data.StudentEntity
import com.example.data.SubjectEntity
import com.example.data.TeacherEntity
import com.example.ui.SchoolViewModel
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@Composable
fun MarkAttendanceBySubjectComponent(
    viewModel: SchoolViewModel,
    students: List<StudentEntity>,
    subjects: List<SubjectEntity>,
    teachers: List<TeacherEntity>,
    attendances: List<AttendanceEntity>,
    modifier: Modifier = Modifier
) {
    var selectedCycle by remember { mutableStateOf("Tous les Cycles") }
    val classes = remember(selectedCycle) {
        SchoolConstants.getClassesForCycle(selectedCycle)
    }
    val defaultSubject = subjects.firstOrNull()?.nom ?: "Mathématiques"

    var selectedClass by remember { mutableStateOf("Garderie") }
    var selectedSubject by remember { mutableStateOf(defaultSubject) }
    var selectedDate by remember { mutableStateOf(viewModel.getCurrentDate()) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // Dialog for motif
    var studentForMotif by remember { mutableStateOf<Pair<StudentEntity, String>?>(null) }
    var motifText by remember { mutableStateOf("") }

    // Students in selected class
    val studentsInClass = remember(students, selectedClass) {
        students.filter { it.classe.equals(selectedClass, ignoreCase = true) }
    }

    // Existing attendances for this session (date + subject + class)
    val sessionAttendances = remember(attendances, selectedDate, selectedSubject, selectedClass) {
        attendances.filter {
            it.dateJour == selectedDate &&
            it.sessionMatiere.equals(selectedSubject, ignoreCase = true) &&
            it.classe.equals(selectedClass, ignoreCase = true)
        }
    }

    // Stats
    val totalStudents = studentsInClass.size
    val presencesCount = sessionAttendances.count { it.statut == "Présent" }
    val absencesCount = sessionAttendances.count { it.statut == "Absent" }
    val retardsCount = sessionAttendances.count { it.statut == "Retard" }
    val justifiesCount = sessionAttendances.count { it.statut == "Justifié" }
    val totalPointes = sessionAttendances.size
    val presenceRate = if (totalPointes > 0) (presencesCount * 100) / totalPointes else 0

    // Assigned teacher for selected subject and class
    val assignedTeacher = remember(teachers, selectedSubject, selectedClass) {
        teachers.firstOrNull { t ->
            t.matieres.contains(selectedSubject, ignoreCase = true) &&
            (t.classesAssignees.isEmpty() || t.classesAssignees.contains(selectedClass, ignoreCase = true))
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Interactive Date Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("date_selector_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Date d'appel :",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (selectedDate != viewModel.getCurrentDate()) {
                            Surface(
                                color = GoldAccent.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedDate = viewModel.getCurrentDate() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Today,
                                        contentDescription = null,
                                        tint = NavyPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Revenir à Aujourd'hui",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { selectedDate = viewModel.getAdjacentDate(selectedDate, -1) },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Jour précédent",
                                tint = NavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Surface(
                            color = NavyPrimary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showDatePickerDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = selectedDate,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = NavyPrimary
                                    )
                                    Text(
                                        text = viewModel.formatDisplayDate(selectedDate),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { selectedDate = viewModel.getAdjacentDate(selectedDate, 1) },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Jour suivant",
                                tint = NavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Subject Carousel (Par Matière)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sélectionner la matière :",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    assignedTeacher?.let { teacher ->
                        Text(
                            text = "Prof: ${teacher.nomComplet}",
                            fontSize = 11.sp,
                            color = NavyPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(subjects) { subj ->
                        val isSelected = selectedSubject.equals(subj.nom, ignoreCase = true)
                        Surface(
                            color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedSubject = subj.nom }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (isSelected) GoldAccent else NavyPrimary.copy(alpha = 0.2f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = subj.code.take(2),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) NavyPrimary else Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = subj.nom,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Cycle & Class Selector Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Cycle & Classe :",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(SchoolConstants.CYCLES) { cycle ->
                        val isSelected = selectedCycle == cycle
                        Surface(
                            color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    selectedCycle = cycle
                                    val available = SchoolConstants.getClassesForCycle(cycle)
                                    if (selectedClass !in available) {
                                        selectedClass = available.first()
                                    }
                                }
                        ) {
                            Text(
                                text = cycle,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
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
                                color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Session Status & Quick Actions Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$selectedSubject • $selectedClass",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Date : $selectedDate • $totalPointes/$totalStudents pointés",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.markAllPresentForSession(
                                    students = studentsInClass,
                                    sessionMatiere = selectedSubject,
                                    dateJour = selectedDate
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_mark_all_present")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tous Présents", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SessionStatItem("Élèves", "$totalStudents", NavyPrimary)
                        SessionStatItem("Présents", "$presencesCount ($presenceRate%)", EmeraldSuccess)
                        SessionStatItem("Absents", "$absencesCount", CrimsonAlert)
                        SessionStatItem("Retards", "$retardsCount", GoldAccent)
                        SessionStatItem("Justifiés", "$justifiesCount", Color(0xFF2563EB))
                    }
                }
            }
        }

        // 5. Student Roster Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Appel des élèves ($totalStudents)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Cliquer pour changer le statut",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 6. Student Roster Rows
        if (studentsInClass.isEmpty()) {
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
                            text = "Aucun élève inscrit en $selectedClass",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(studentsInClass) { student ->
                val currentAttendance = sessionAttendances.firstOrNull { it.studentId == student.id }
                StudentAttendanceCard(
                    student = student,
                    currentAttendance = currentAttendance,
                    onStatusSelected = { status ->
                        if (status == "Absent" || status == "Retard" || status == "Justifié") {
                            motifText = currentAttendance?.motif ?: ""
                            studentForMotif = Pair(student, status)
                        } else {
                            viewModel.markAttendance(
                                student = student,
                                sessionMatiere = selectedSubject,
                                statut = status,
                                motif = "",
                                dateJour = selectedDate
                            )
                        }
                    },
                    onEditMotif = {
                        motifText = currentAttendance?.motif ?: ""
                        studentForMotif = Pair(student, currentAttendance?.statut ?: "Absent")
                    }
                )
            }
        }
    }

    // Motif Dialog
    studentForMotif?.let { (student, status) ->
        val quickMotifs = when (status) {
            "Retard" -> listOf("Retard transport / Taxi-brousse", "Panne réveil", "Obligation familiale", "Retard < 15min")
            "Justifié" -> listOf("Certificat médical", "Permission parentale", "Convocation officielle", "Décès familial")
            else -> listOf("Maladie non justifiée", "Absence sans motif", "Raison familiale", "Intempéries / Pluie")
        }

        AlertDialog(
            onDismissRequest = { studentForMotif = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = when (status) {
                        "Absent" -> CrimsonAlert
                        "Retard" -> GoldAccent
                        else -> Color(0xFF2563EB)
                    }
                    Surface(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = status,
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text("Motif de l'assiduité", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Élève : ${student.nomComplet} (${student.classe})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Séance : $selectedSubject du $selectedDate",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Suggestions rapides :",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        quickMotifs.forEach { preset ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { motifText = preset }
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = motifText,
                        onValueChange = { motifText = it },
                        label = { Text("Motif ou remarque personnalisée") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markAttendance(
                            student = student,
                            sessionMatiere = selectedSubject,
                            statut = status,
                            motif = motifText.trim(),
                            dateJour = selectedDate
                        )
                        studentForMotif = null
                        motifText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Enregistrer le statut")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.markAttendance(
                            student = student,
                            sessionMatiere = selectedSubject,
                            statut = status,
                            motif = "",
                            dateJour = selectedDate
                        )
                        studentForMotif = null
                        motifText = ""
                    }
                ) {
                    Text("Sans motif")
                }
            }
        )
    }

    // Date Picker Dialog
    if (showDatePickerDialog) {
        val today = remember { viewModel.getCurrentDate() }
        val recentDates = remember {
            listOf(
                today,
                viewModel.getAdjacentDate(today, -1),
                viewModel.getAdjacentDate(today, -2),
                viewModel.getAdjacentDate(today, -3),
                viewModel.getAdjacentDate(today, -4),
                viewModel.getAdjacentDate(today, -7)
            )
        }
        var customDateInput by remember { mutableStateOf(selectedDate) }

        AlertDialog(
            onDismissRequest = { showDatePickerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = NavyPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Choisir la date d'appel", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Sélectionner une journée scolaire récente :",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        recentDates.forEach { d ->
                            val isCurrent = d == selectedDate
                            Surface(
                                color = if (isCurrent) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedDate = d
                                        showDatePickerDialog = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = d,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = viewModel.formatDisplayDate(d),
                                        fontSize = 11.sp,
                                        color = if (isCurrent) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ou saisir une date (JJ/MM/AAAA) :",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = customDateInput,
                        onValueChange = { customDateInput = it },
                        label = { Text("Date (ex: 09/09/2026)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customDateInput.isNotBlank()) {
                            selectedDate = customDateInput.trim()
                        }
                        showDatePickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Valider")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Fermer")
                }
            }
        )
    }
}

@Composable
private fun StudentAttendanceCard(
    student: StudentEntity,
    currentAttendance: AttendanceEntity?,
    onStatusSelected: (String) -> Unit,
    onEditMotif: () -> Unit
) {
    val currentStatus = currentAttendance?.statut

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Student Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        color = NavyPrimary.copy(alpha = 0.1f),
                        shape = CircleShape,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = student.prenoms.firstOrNull()?.toString() ?: "E",
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = student.nomComplet,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${student.matricule} • ${student.classe}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Current status badge
                if (currentStatus != null) {
                    val statusColor = when (currentStatus) {
                        "Présent" -> EmeraldSuccess
                        "Absent" -> CrimsonAlert
                        "Retard" -> GoldAccent
                        else -> Color(0xFF2563EB)
                    }
                    Surface(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = currentStatus,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Non pointé",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Reason / Motif if available
            if (currentAttendance != null && currentAttendance.motif.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Motif : ${currentAttendance.motif}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier motif",
                        tint = NavyPrimary,
                        modifier = Modifier
                            .size(14.dp)
                            .clickable { onEditMotif() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4 Status Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusSelectButton(
                    label = "Présent",
                    isSelected = currentStatus == "Présent",
                    activeColor = EmeraldSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusSelected("Présent") }
                )
                StatusSelectButton(
                    label = "Absent",
                    isSelected = currentStatus == "Absent",
                    activeColor = CrimsonAlert,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusSelected("Absent") }
                )
                StatusSelectButton(
                    label = "Retard",
                    isSelected = currentStatus == "Retard",
                    activeColor = GoldAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusSelected("Retard") }
                )
                StatusSelectButton(
                    label = "Justifié",
                    isSelected = currentStatus == "Justifié",
                    activeColor = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusSelected("Justifié") }
                )
            }
        }
    }
}

@Composable
private fun StatusSelectButton(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) activeColor else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SessionStatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
