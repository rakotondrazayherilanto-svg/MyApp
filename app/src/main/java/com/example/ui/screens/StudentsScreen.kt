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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SchoolConstants
import com.example.data.StudentEntity
import com.example.ui.SchoolViewModel
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@Composable
fun StudentsScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.students.collectAsState()
    val fees by viewModel.fees.collectAsState()
    val grades by viewModel.grades.collectAsState()
    val attendances by viewModel.attendances.collectAsState()

    var selectedCycleFilter by remember { mutableStateOf("Tous les Cycles") }
    var selectedClassFilter by remember { mutableStateOf("Toutes") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedStudentForDetail by remember { mutableStateOf<StudentEntity?>(null) }
    var studentToDelete by remember { mutableStateOf<StudentEntity?>(null) }

    val availableClasses = remember(selectedCycleFilter) {
        listOf("Toutes") + SchoolConstants.getClassesForCycle(selectedCycleFilter)
    }

    val filteredStudents = students.filter { st ->
        (selectedCycleFilter == "Tous les Cycles" || SchoolConstants.getCycleForClass(st.classe) == selectedCycleFilter) &&
        (selectedClassFilter == "Toutes" || SchoolConstants.classMatches(st.classe, selectedClassFilter)) &&
        (searchQuery.isBlank() ||
         st.nomComplet.contains(searchQuery, ignoreCase = true) ||
         st.matricule.contains(searchQuery, ignoreCase = true) ||
         st.contactTuteur.contains(searchQuery, ignoreCase = true))
    }

    Box(modifier = modifier.fillMaxSize().testTag("students_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title & Search
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Registre des Élèves",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${filteredStudents.size} inscrits affichés",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().testTag("student_search_field"),
                    placeholder = { Text("Rechercher un élève par nom, matricule...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Cycle & Class filters
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Cycles
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
                                        selectedClassFilter = "Toutes"
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

                    // Classes in cycle
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(availableClasses) { cl ->
                            val isSelected = selectedClassFilter == cl
                            Surface(
                                color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { selectedClassFilter = cl }
                            ) {
                                Text(
                                    text = cl,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (filteredStudents.isEmpty()) {
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
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Aucun élève trouvé",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredStudents) { student ->
                    StudentCard(
                        student = student,
                        formattedEcolage = viewModel.formatAriary(student.ecolageMensuelAr),
                        onClick = { selectedStudentForDetail = student },
                        onDelete = { studentToDelete = student }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }

        // Add Student FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = NavyPrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_student_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nouvel Élève", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Add Student Dialog
    if (showAddDialog) {
        AddStudentDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { nom, prenoms, classe, sexe, dateNaissance, tuteur, contact, adresse, ecolageAr ->
                viewModel.addStudent(nom, prenoms, classe, sexe, dateNaissance, tuteur, contact, adresse, ecolageAr)
                showAddDialog = false
            }
        )
    }

    // Student Detail Dialog
    selectedStudentForDetail?.let { student ->
        val studentFees = fees.filter { it.studentId == student.id }
        val studentGrades = grades.filter { it.studentId == student.id }
        val studentAttendances = attendances.filter { it.studentId == student.id }

        StudentDetailDialog(
            student = student,
            fees = studentFees,
            grades = studentGrades,
            attendances = studentAttendances,
            formatAriary = { viewModel.formatAriary(it) },
            onDismiss = { selectedStudentForDetail = null }
        )
    }

    // Delete Student Dialog
    studentToDelete?.let { student ->
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("Supprimer l'élève") },
            text = { Text("Êtes-vous sûr de vouloir supprimer l'élève ${student.nomComplet} (${student.matricule}) ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStudent(student)
                        studentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { studentToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun StudentCard(
    student: StudentEntity,
    formattedEcolage: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with initials
            Surface(
                color = if (student.sexe == "M") NavyPrimary else Color(0xFFD946EF),
                shape = CircleShape,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val initials = (student.nom.take(1) + student.prenoms.take(1)).uppercase()
                    Text(
                        text = initials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = student.nomComplet,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = student.classe,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        color = GoldAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = SchoolConstants.getCycleForClass(student.classe),
                            color = GoldAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = student.matricule,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tuteur: ${student.nomTuteur} (${student.contactTuteur})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = GoldAccent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = formattedEcolage,
                        color = GoldAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddStudentDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, String, String, Long) -> Unit
) {
    var nom by remember { mutableStateOf("") }
    var prenoms by remember { mutableStateOf("") }

    var selectedCycle by remember { mutableStateOf("Tous les Cycles") }
    val classes = remember(selectedCycle) {
        SchoolConstants.getClassesForCycle(selectedCycle)
    }
    var selectedClasse by remember { mutableStateOf("Garderie") }
    var expandedClasse by remember { mutableStateOf(false) }

    var sexe by remember { mutableStateOf("M") }
    var dateNaissance by remember { mutableStateOf("15/04/2020") }
    var nomTuteur by remember { mutableStateOf("") }
    var contactTuteur by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("Fenoarivobe") }
    var ecolageText by remember { mutableStateOf("25000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Inscription Nouvel Élève",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
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
                        label = { Text("Nom de famille *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = prenoms,
                        onValueChange = { prenoms = it },
                        label = { Text("Prénoms *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    // Cycle selector
                    Text(
                        text = "Filtrer par cycle :",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(SchoolConstants.CYCLES) { cycle ->
                            val isSelected = selectedCycle == cycle
                            Surface(
                                color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedCycle = cycle
                                        val available = SchoolConstants.getClassesForCycle(cycle)
                                        if (selectedClasse !in available) {
                                            selectedClasse = available.first()
                                            ecolageText = SchoolConstants.getDefaultTuitionForClass(selectedClasse).toString()
                                        }
                                    }
                            ) {
                                Text(
                                    text = cycle,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedClasse,
                        onExpandedChange = { expandedClasse = it }
                    ) {
                        OutlinedTextField(
                            value = "$selectedClasse (${SchoolConstants.getCycleForClass(selectedClasse)})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Classe *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedClasse) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedClasse,
                            onDismissRequest = { expandedClasse = false }
                        ) {
                            classes.forEach { cl ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(cl, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                text = SchoolConstants.getCycleForClass(cl),
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedClasse = cl
                                        ecolageText = SchoolConstants.getDefaultTuitionForClass(cl).toString()
                                        expandedClasse = false
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sexe :", fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = if (sexe == "M") NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { sexe = "M" }
                            ) {
                                Text(
                                    text = "Masculin (M)",
                                    color = if (sexe == "M") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = if (sexe == "F") Color(0xFFD946EF) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { sexe = "F" }
                            ) {
                                Text(
                                    text = "Féminin (F)",
                                    color = if (sexe == "F") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = dateNaissance,
                        onValueChange = { dateNaissance = it },
                        label = { Text("Date de naissance (JJ/MM/AAAA)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = nomTuteur,
                        onValueChange = { nomTuteur = it },
                        label = { Text("Nom du Tuteur / Parent *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = contactTuteur,
                        onValueChange = { contactTuteur = it },
                        label = { Text("Téléphone Tuteur (ex: 034...) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = adresse,
                        onValueChange = { adresse = it },
                        label = { Text("Adresse / Quartier") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = ecolageText,
                        onValueChange = { ecolageText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Écolage mensuel en Ariary (Ar)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        suffix = { Text("Ar", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nom.isNotBlank() && prenoms.isNotBlank()) {
                        val ecolageAr = ecolageText.toLongOrNull() ?: 35000L
                        onConfirm(
                            nom, prenoms, selectedClasse, sexe,
                            dateNaissance, nomTuteur, contactTuteur, adresse, ecolageAr
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Inscrire l'élève")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
private fun StudentDetailDialog(
    student: StudentEntity,
    fees: List<com.example.data.FeePaymentEntity>,
    grades: List<com.example.data.GradeEntity>,
    attendances: List<com.example.data.AttendanceEntity>,
    formatAriary: (Long) -> String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Fermer la fiche")
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = if (student.sexe == "M") NavyPrimary else Color(0xFFD946EF),
                            shape = CircleShape,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (student.nom.take(1) + student.prenoms.take(1)).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = student.nomComplet,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${student.matricule} • Classe ${student.classe}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                item {
                    HorizontalDivider()
                }

                // Civil info
                item {
                    Text(text = "Renseignements Généraux", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "• Date de naissance : ${student.dateNaissance}", fontSize = 12.sp)
                    Text(text = "• Tuteur : ${student.nomTuteur}", fontSize = 12.sp)
                    Text(text = "• Contact : ${student.contactTuteur}", fontSize = 12.sp)
                    Text(text = "• Adresse : ${student.adresse}", fontSize = 12.sp)
                    Text(text = "• Écolage fixé : ${formatAriary(student.ecolageMensuelAr)}/mois", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GoldAccent)
                }

                item {
                    HorizontalDivider()
                }

                // Fees
                item {
                    val totalPaid = fees.sumOf { it.montantAr }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Écolages & Frais (Ar)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = formatAriary(totalPaid), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EmeraldSuccess)
                    }
                    if (fees.isEmpty()) {
                        Text(text = "Aucun versement enregistré.", fontSize = 11.sp, color = Color.Gray)
                    } else {
                        fees.forEach { fee ->
                            Text(
                                text = "• ${fee.mois} : ${formatAriary(fee.montantAr)} (${fee.modePaiement} - ${fee.datePaiement})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    HorizontalDivider()
                }

                // Grades summary
                item {
                    Text(text = "Notes & Résultats", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    if (grades.isEmpty()) {
                        Text(text = "Aucune note saisie pour le moment.", fontSize = 11.sp, color = Color.Gray)
                    } else {
                        val average = grades.map { it.noteSur20 * it.coefficient }.sum() / grades.sumOf { it.coefficient }
                        Text(
                            text = "Moyenne calculée : ${String.format(java.util.Locale.US, "%.2f", average)} / 20",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (average >= 10.0) EmeraldSuccess else MaterialTheme.colorScheme.error
                        )
                        grades.forEach { g ->
                            Text(
                                text = "• ${g.matiere} : ${g.noteSur20}/20 (coef ${g.coefficient}) - ${g.typeDevoir}",
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                item {
                    HorizontalDivider()
                }

                // Attendances
                item {
                    val presences = attendances.count { it.statut == "Présent" }
                    val absences = attendances.count { it.statut == "Absent" }
                    val retards = attendances.count { it.statut == "Retard" }
                    Text(text = "Assiduité & Présences", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = "• $presences Présent(s)   • $absences Absent(s)   • $retards Retard(s)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}
