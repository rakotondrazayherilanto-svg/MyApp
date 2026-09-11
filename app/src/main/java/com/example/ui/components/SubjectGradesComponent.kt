package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GradeEntity
import com.example.data.SchoolConstants
import com.example.data.StudentEntity
import com.example.data.SubjectEntity
import com.example.ui.SchoolViewModel
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary
import java.util.Locale

/**
 * UI component to record and list academic grades for students per subject.
 * Supports:
 *  1. Selecting subject and viewing its recorded grades with stats.
 *  2. Individual student grade recording with coefficient & evaluation type.
 *  3. Class-wide rapid grade sheet entry for an entire class in the selected subject.
 *  4. Filtering by class, evaluation type, and academic term (Trimestre).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectGradesComponent(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val students by viewModel.students.collectAsState()
    val grades by viewModel.grades.collectAsState()

    // Default subject selection
    var selectedSubjectNom by remember(subjects) {
        mutableStateOf(subjects.firstOrNull()?.nom ?: "Mathématiques")
    }

    var selectedCycleFilter by remember { mutableStateOf("Tous les Cycles") }
    val classes = remember(selectedCycleFilter) {
        listOf("Toutes") + SchoolConstants.getClassesForCycle(selectedCycleFilter)
    }
    var selectedClass by remember { mutableStateOf("Toutes") }
    var selectedTrimestre by remember { mutableStateOf("Trimestre 1") }
    var selectedTypeFilter by remember { mutableStateOf("Tous") }
    var searchQuery by remember { mutableStateOf("") }

    // Sub-tab: 0 = "Liste des Notes", 1 = "Saisie Rapide Classe"
    var viewSubTab by remember { mutableStateOf(0) }

    // Dialogs
    var showAddSingleGradeDialog by remember { mutableStateOf(false) }
    var gradeToDelete by remember { mutableStateOf<GradeEntity?>(null) }

    val activeSubject = subjects.find { it.nom == selectedSubjectNom }
    val evaluationTypes = listOf("Tous", "Devoir Surveillé", "Interrogation", "Examen")
    val trimestres = listOf("Trimestre 1", "Trimestre 2", "Trimestre 3")

    // Filter grades for the selected subject
    val subjectGrades = grades.filter { g ->
        g.matiere.equals(selectedSubjectNom, ignoreCase = true) &&
        (selectedCycleFilter == "Tous les Cycles" || SchoolConstants.getCycleForClass(g.classe) == selectedCycleFilter) &&
        (selectedClass == "Toutes" || SchoolConstants.classMatches(g.classe, selectedClass)) &&
        g.trimestre.equals(selectedTrimestre, ignoreCase = true) &&
        (selectedTypeFilter == "Tous" || g.typeDevoir.equals(selectedTypeFilter, ignoreCase = true)) &&
        (searchQuery.isBlank() || g.studentNom.contains(searchQuery, ignoreCase = true) || g.studentMatricule.contains(searchQuery, ignoreCase = true))
    }

    // Performance statistics for current subject filter
    val stats by remember(subjectGrades) {
        derivedStateOf {
            if (subjectGrades.isEmpty()) {
                SubjectStats(total = 0, average = 0.0, max = 0.0, min = 0.0, passRate = 0.0)
            } else {
                val sumNotes = subjectGrades.sumOf { it.noteSur20 * it.coefficient }
                val sumCoefs = subjectGrades.sumOf { it.coefficient }
                val avg = if (sumCoefs > 0) sumNotes / sumCoefs else 0.0
                val maxNote = subjectGrades.maxOf { it.noteSur20 }
                val minNote = subjectGrades.minOf { it.noteSur20 }
                val passingCount = subjectGrades.count { it.noteSur20 >= 10.0 }
                val rate = (passingCount.toDouble() / subjectGrades.size) * 100.0
                SubjectStats(
                    total = subjectGrades.size,
                    average = avg,
                    max = maxNote,
                    min = minNote,
                    passRate = rate
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("subject_grades_component")
    ) {
        // --- 1. Subject Selector Bar ---
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Matière Académique :",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Coef: ${activeSubject?.coefficientDefaut ?: 2} • Prof: ${activeSubject?.enseignantNom?.ifEmpty { "Non assigné" } ?: "LP3F"}",
                        fontSize = 12.sp,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(subjects) { subj ->
                        val isSelected = subj.nom == selectedSubjectNom
                        Surface(
                            color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedSubjectNom = subj.nom }
                                .testTag("subject_chip_${subj.code}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = if (isSelected) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = subj.nom,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Filter Bar (Classe & Trimestre) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trimestre Selector
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(trimestres) { tr ->
                    val isSel = selectedTrimestre == tr
                    Surface(
                        color = if (isSel) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedTrimestre = tr }
                    ) {
                        Text(
                            text = tr,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Button to open individual grade recording dialog
            Button(
                onClick = { showAddSingleGradeDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("add_grade_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Saisir Note", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Cycle and Class Filter Chips
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Text(
                        text = "Cycle :",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(end = 2.dp)
                    )
                }
                items(SchoolConstants.CYCLES) { cycle ->
                    val isSel = selectedCycleFilter == cycle
                    Surface(
                        color = if (isSel) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedCycleFilter = cycle
                                selectedClass = "Toutes"
                            }
                    ) {
                        Text(
                            text = cycle,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Text(
                        text = "Classe :",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(end = 2.dp)
                    )
                }
                items(classes) { cl ->
                    val isSel = selectedClass == cl
                    Surface(
                        color = if (isSel) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedClass = cl }
                    ) {
                        Text(
                            text = cl,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // --- 3. Subject Statistics Overview Card ---
        Card(
            colors = CardDefaults.cardColors(containerColor = NavyPrimary.copy(alpha = 0.06f)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatMetricItem(
                    label = "Moyenne /20",
                    value = if (stats.total > 0) String.format(Locale.US, "%.2f", stats.average) else "--",
                    color = getGradeColor(stats.average)
                )
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                StatMetricItem(
                    label = "Note Max",
                    value = if (stats.total > 0) String.format(Locale.US, "%.1f", stats.max) else "--",
                    color = EmeraldSuccess
                )
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                StatMetricItem(
                    label = "Taux Réussite",
                    value = if (stats.total > 0) "${stats.passRate.toInt()}%" else "--",
                    color = if (stats.passRate >= 50.0) EmeraldSuccess else Color(0xFFEF4444)
                )
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                StatMetricItem(
                    label = "Évaluations",
                    value = "${stats.total}",
                    color = NavyPrimary
                )
            }
        }

        // --- 4. Sub Tabs: "Liste des Notes" vs "Saisie Rapide Classe" ---
        TabRow(
            selectedTabIndex = viewSubTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = viewSubTab == 0,
                onClick = { viewSubTab = 0 },
                text = { Text("Liste des Notes (${subjectGrades.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
            )
            Tab(
                selected = viewSubTab == 1,
                onClick = { viewSubTab = 1 },
                text = { Text("Saisie Rapide Classe", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
            )
        }

        // --- 5. Content Views ---
        if (viewSubTab == 0) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher un élève par nom ou matricule...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            if (subjectGrades.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = NavyPrimary.copy(alpha = 0.1f),
                            shape = CircleShape,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Grade,
                                    contentDescription = null,
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Text(
                            text = "Aucune note enregistrée pour $selectedSubjectNom",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Filtres actuels: $selectedClass • $selectedTrimestre",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { showAddSingleGradeDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Enregistrer une note maintenant")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(subjectGrades, key = { it.id }) { grade ->
                        SubjectGradeItemCard(
                            grade = grade,
                            onDelete = { gradeToDelete = grade }
                        )
                    }
                }
            }
        } else {
            // Rapid class grade entry sheet
            RapidClassGradeEntrySheet(
                viewModel = viewModel,
                selectedSubjectNom = selectedSubjectNom,
                selectedTrimestre = selectedTrimestre,
                defaultCoefficient = activeSubject?.coefficientDefaut ?: 2,
                allStudents = students
            )
        }
    }

    // Dialog for adding a single grade
    if (showAddSingleGradeDialog) {
        AddSingleGradeDialog(
            subjectNom = selectedSubjectNom,
            defaultCoef = activeSubject?.coefficientDefaut ?: 2,
            trimestre = selectedTrimestre,
            students = students,
            onDismiss = { showAddSingleGradeDialog = false },
            onConfirm = { student, typeDevoir, noteSur20, coef ->
                viewModel.addGrade(
                    student = student,
                    matiere = selectedSubjectNom,
                    trimestre = selectedTrimestre,
                    typeDevoir = typeDevoir,
                    noteSur20 = noteSur20,
                    coefficient = coef
                )
                showAddSingleGradeDialog = false
            }
        )
    }

    // Delete confirmation dialog
    if (gradeToDelete != null) {
        val grade = gradeToDelete!!
        AlertDialog(
            onDismissRequest = { gradeToDelete = null },
            title = { Text("Supprimer cette note ?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Voulez-vous supprimer la note de ${grade.noteSur20}/20 en ${grade.matiere} pour l'élève ${grade.studentNom} (${grade.studentMatricule}) ?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteGrade(grade)
                        gradeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { gradeToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

/**
 * Card displaying an individual student's grade in this subject.
 */
@Composable
fun SubjectGradeItemCard(
    grade: GradeEntity,
    onDelete: () -> Unit
) {
    val gradeColor = getGradeColor(grade.noteSur20)

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Score Badge
                Surface(
                    color = gradeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, gradeColor.copy(alpha = 0.4f)),
                    modifier = Modifier.size(54.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.1f", grade.noteSur20),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = gradeColor
                        )
                        Text(
                            text = "/20",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = gradeColor.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = grade.studentNom,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = NavyPrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = grade.classe,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = grade.typeDevoir,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• Coef ${grade.coefficient}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldAccent
                        )
                    }
                    if (grade.dateSaisie.isNotBlank()) {
                        Text(
                            text = "Saisie: ${grade.dateSaisie}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer la note",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Rapid class grade entry sheet: Displays roster for a class and allows inline score entry.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RapidClassGradeEntrySheet(
    viewModel: SchoolViewModel,
    selectedSubjectNom: String,
    selectedTrimestre: String,
    defaultCoefficient: Int,
    allStudents: List<StudentEntity>
) {
    var currentCycle by remember { mutableStateOf("Tous les Cycles") }
    val classes = remember(currentCycle) {
        SchoolConstants.getClassesForCycle(currentCycle)
    }
    var currentClass by remember { mutableStateOf("Garderie") }
    var evaluationType by remember { mutableStateOf("Devoir Surveillé") }
    var coefficientText by remember(defaultCoefficient) { mutableStateOf(defaultCoefficient.toString()) }

    // Map of studentId -> grade input string
    val gradeInputs = remember { mutableStateMapOf<Long, String>() }
    val savedConfirmationMap = remember { mutableStateMapOf<Long, Boolean>() }

    val classStudents = allStudents.filter { it.classe.equals(currentClass, ignoreCase = true) }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Configuration de la Saisie Rapide",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NavyPrimary
                    )

                    // Cycle selection
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(SchoolConstants.CYCLES) { cycle ->
                            val isSel = currentCycle == cycle
                            Surface(
                                color = if (isSel) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        currentCycle = cycle
                                        val available = SchoolConstants.getClassesForCycle(cycle)
                                        if (currentClass !in available) {
                                            currentClass = available.first()
                                            gradeInputs.clear()
                                            savedConfirmationMap.clear()
                                        }
                                    }
                            ) {
                                Text(
                                    text = cycle,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Class selection
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(classes) { cl ->
                            val isSel = currentClass == cl
                            Surface(
                                color = if (isSel) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        currentClass = cl
                                        gradeInputs.clear()
                                        savedConfirmationMap.clear()
                                    }
                            ) {
                                Text(
                                    text = cl,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Evaluation Type
                        val evalTypes = listOf("Devoir Surveillé", "Interrogation", "Examen")
                        var expEval by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expEval,
                            onExpandedChange = { expEval = it },
                            modifier = Modifier.weight(1.5f)
                        ) {
                            OutlinedTextField(
                                value = evaluationType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Épreuve", fontSize = 11.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expEval) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                singleLine = true
                            )
                            ExposedDropdownMenu(
                                expanded = expEval,
                                onDismissRequest = { expEval = false }
                            ) {
                                evalTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type, fontSize = 12.sp) },
                                        onClick = {
                                            evaluationType = type
                                            expEval = false
                                        }
                                    )
                                }
                            }
                        }

                        // Coefficient input
                        OutlinedTextField(
                            value = coefficientText,
                            onValueChange = { coefficientText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Coef", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }

                    Text(
                        text = "${classStudents.size} élèves en $currentClass pour $selectedSubjectNom ($selectedTrimestre)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (classStudents.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Aucun élève inscrit en $currentClass.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(classStudents, key = { it.id }) { student ->
                val enteredGrade = gradeInputs[student.id] ?: ""
                val isSaved = savedConfirmationMap[student.id] ?: false

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSaved) EmeraldSuccess.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = if (isSaved) androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f)) else null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = student.nomComplet,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = student.matricule,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = enteredGrade,
                                onValueChange = { input ->
                                    val cleaned = input.replace(',', '.')
                                    if (cleaned.isEmpty() || cleaned.toDoubleOrNull() != null && cleaned.toDouble() <= 20.0) {
                                        gradeInputs[student.id] = cleaned
                                        savedConfirmationMap[student.id] = false
                                    }
                                },
                                placeholder = { Text("/20", fontSize = 12.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                                modifier = Modifier
                                    .width(72.dp)
                                    .height(48.dp)
                                    .testTag("grade_input_${student.id}")
                            )

                            Button(
                                onClick = {
                                    val note = enteredGrade.toDoubleOrNull()
                                    if (note != null && note in 0.0..20.0) {
                                        val coef = coefficientText.toIntOrNull() ?: defaultCoefficient
                                        viewModel.addGrade(
                                            student = student,
                                            matiere = selectedSubjectNom,
                                            trimestre = selectedTrimestre,
                                            typeDevoir = evaluationType,
                                            noteSur20 = note,
                                            coefficient = coef
                                        )
                                        savedConfirmationMap[student.id] = true
                                    }
                                },
                                enabled = enteredGrade.toDoubleOrNull() != null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSaved) EmeraldSuccess else NavyPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(40.dp)
                            ) {
                                if (isSaved) {
                                    Icon(Icons.Default.Check, contentDescription = "Enregistré", modifier = Modifier.size(16.dp))
                                } else {
                                    Text("Valider", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Save All Button
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        val coef = coefficientText.toIntOrNull() ?: defaultCoefficient
                        classStudents.forEach { st ->
                            val input = gradeInputs[st.id]
                            val note = input?.toDoubleOrNull()
                            if (note != null && note in 0.0..20.0) {
                                viewModel.addGrade(
                                    student = st,
                                    matiere = selectedSubjectNom,
                                    trimestre = selectedTrimestre,
                                    typeDevoir = evaluationType,
                                    noteSur20 = note,
                                    coefficient = coef
                                )
                                savedConfirmationMap[st.id] = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_all_grades_button")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enregistrer Toutes les Notes de la Classe", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

/**
 * Dialog for entering a single grade for an individual student in this subject.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSingleGradeDialog(
    subjectNom: String,
    defaultCoef: Int,
    trimestre: String,
    students: List<StudentEntity>,
    onDismiss: () -> Unit,
    onConfirm: (StudentEntity, String, Double, Int) -> Unit
) {
    var selectedStudent by remember { mutableStateOf(students.firstOrNull()) }
    var expandedStudentDropdown by remember { mutableStateOf(false) }

    val evalTypes = listOf("Devoir Surveillé", "Interrogation", "Examen")
    var selectedType by remember { mutableStateOf("Devoir Surveillé") }
    var expandedTypeDropdown by remember { mutableStateOf(false) }

    var noteText by remember { mutableStateOf("") }
    var coefText by remember { mutableStateOf(defaultCoef.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Grade,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Saisie de Note • $subjectNom", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(trimestre, fontSize = 12.sp, color = GoldAccent, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Student selector
                ExposedDropdownMenuBox(
                    expanded = expandedStudentDropdown,
                    onExpandedChange = { expandedStudentDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedStudent?.let { "${it.nomComplet} (${it.classe})" } ?: "Sélectionner un élève",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Élève *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStudentDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStudentDropdown,
                        onDismissRequest = { expandedStudentDropdown = false }
                    ) {
                        students.forEach { st ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("${st.nomComplet} • ${st.classe}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(st.matricule, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                onClick = {
                                    selectedStudent = st
                                    expandedStudentDropdown = false
                                }
                            )
                        }
                    }
                }

                // Evaluation Type
                ExposedDropdownMenuBox(
                    expanded = expandedTypeDropdown,
                    onExpandedChange = { expandedTypeDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type d'Épreuve *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTypeDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTypeDropdown,
                        onDismissRequest = { expandedTypeDropdown = false }
                    ) {
                        evalTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    expandedTypeDropdown = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Note /20
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { input ->
                            noteText = input.replace(',', '.')
                        },
                        label = { Text("Note /20 *") },
                        placeholder = { Text("ex: 14.5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1.2f),
                        singleLine = true
                    )

                    // Coefficient
                    OutlinedTextField(
                        value = coefText,
                        onValueChange = { coefText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Coef") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.8f),
                        singleLine = true
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val st = selectedStudent
                    if (st == null) {
                        errorMessage = "Veuillez sélectionner un élève."
                        return@Button
                    }
                    val note = noteText.toDoubleOrNull()
                    if (note == null || note < 0.0 || note > 20.0) {
                        errorMessage = "La note doit être comprise entre 0.0 et 20.0."
                        return@Button
                    }
                    val coef = coefText.toIntOrNull() ?: defaultCoef
                    errorMessage = null
                    onConfirm(st, selectedType, note, coef)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

/**
 * Metric summary sub-item
 */
@Composable
fun StatMetricItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Color coding for grades /20 according to academic scale.
 */
fun getGradeColor(note: Double): Color {
    return when {
        note >= 14.0 -> EmeraldSuccess
        note >= 10.0 -> Color(0xFF2563EB) // Blue
        note >= 8.0 -> GoldAccent
        else -> Color(0xFFDC2626) // Red
    }
}

data class SubjectStats(
    val total: Int,
    val average: Double,
    val max: Double,
    val min: Double,
    val passRate: Double
)
