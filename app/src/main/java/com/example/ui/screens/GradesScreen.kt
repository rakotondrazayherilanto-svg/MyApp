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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GradeEntity
import com.example.data.SchoolConstants
import com.example.data.SchoolProfile
import com.example.data.SubjectEntity
import com.example.data.StudentEntity
import com.example.ui.SchoolViewModel
import com.example.ui.components.SubjectGradesComponent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary
import com.example.utils.PdfExporter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.appSettings.collectAsState()
    val schoolProfile = settings.schoolProfile
    val grades by viewModel.grades.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val students by viewModel.students.collectAsState()

    // 0: Notes par Matière, 1: Bulletins par Élève, 2: Matières Officielles
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedTrimestre by remember { mutableStateOf("Trimestre 1") }
    var selectedCycleFilter by remember { mutableStateOf("Tous les Cycles") }
    var selectedClassFilter by remember { mutableStateOf("Toutes") }

    val classList = remember(selectedCycleFilter) {
        listOf("Toutes") + SchoolConstants.getClassesForCycle(selectedCycleFilter)
    }

    var showAddGradeDialog by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var gradeToDelete by remember { mutableStateOf<GradeEntity?>(null) }
    var subjectToDelete by remember { mutableStateOf<SubjectEntity?>(null) }

    val trimestres = listOf("Trimestre 1", "Trimestre 2", "Trimestre 3")

    val filteredGrades = grades.filter { g ->
        g.trimestre.equals(selectedTrimestre, ignoreCase = true) &&
        (selectedCycleFilter == "Tous les Cycles" || SchoolConstants.getCycleForClass(g.classe) == selectedCycleFilter) &&
        (selectedClassFilter == "Toutes" || SchoolConstants.classMatches(g.classe, selectedClassFilter))
    }

    Box(modifier = modifier.fillMaxSize().testTag("grades_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header card
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = "Évaluations, Bulletins & Matières",
                        color = Color(0xFFD1E4FF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Gestion Académique LP3F",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${subjects.size} matières enregistrées • Notes /20 avec coefficients",
                        color = GoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 3 Tabs: Notes par Matière, Bulletins par Élève, Matières
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Par Matière", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Bulletins", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Matières (${subjects.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // UI component to record and list academic grades for students per subject
                    SubjectGradesComponent(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                1 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Trimestre chips
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(trimestres) { t ->
                                    val isSelected = selectedTrimestre == t
                                    Surface(
                                        color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .clickable { selectedTrimestre = t }
                                    ) {
                                        Text(
                                            text = t,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Cycle and Class filters
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

                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(classList) { cl ->
                                        val isSelected = selectedClassFilter == cl
                                        Surface(
                                            color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable { selectedClassFilter = cl }
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

                        // Group grades by student to display bulletin cards
                        val studentsInView = students.filter { st ->
                            (selectedCycleFilter == "Tous les Cycles" || SchoolConstants.getCycleForClass(st.classe) == selectedCycleFilter) &&
                            (selectedClassFilter == "Toutes" || SchoolConstants.classMatches(st.classe, selectedClassFilter))
                        }

                        if (studentsInView.isEmpty()) {
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
                                            text = "Aucun élève dans cette classe.",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            items(studentsInView) { student ->
                                val studentGrades = filteredGrades.filter { it.studentId == student.id }
                                StudentBulletinCard(
                                    student = student,
                                    grades = studentGrades,
                                    trimestre = selectedTrimestre,
                                    schoolProfile = schoolProfile,
                                    onDeleteGrade = { gradeToDelete = it }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(70.dp))
                        }
                    }
                }
                2 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Liste des Matières Officielles",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        items(subjects) { subject ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
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
                                            color = NavyPrimary.copy(alpha = 0.15f),
                                            shape = CircleShape,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = subject.code,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 11.sp,
                                                    color = NavyPrimary
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = subject.nom,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "Prof: ${subject.enseignantNom.ifEmpty { "Non assigné" }}",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = GoldAccent.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Coef ${subject.coefficientDefaut}",
                                                color = GoldAccent,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        IconButton(onClick = { subjectToDelete = subject }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Supprimer matière",
                                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(70.dp))
                        }
                    }
                }
            }
        }

        // FAB to add grade or subject when on Bulletins or Matières tab
        if (selectedTab != 0) {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 1) showAddGradeDialog = true else showAddSubjectDialog = true
                },
                containerColor = if (selectedTab == 1) GoldAccent else NavyPrimary,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .testTag("add_grade_or_subject_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (selectedTab == 1) "Saisir Note" else "Ajouter Matière",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Dialog: Add Grade
    if (showAddGradeDialog) {
        AddGradeDialog(
            students = students,
            subjects = subjects,
            currentTrimestre = selectedTrimestre,
            onDismiss = { showAddGradeDialog = false },
            onConfirm = { student, matiere, trimestre, typeDevoir, note, coef ->
                viewModel.addGrade(student, matiere, trimestre, typeDevoir, note, coef)
                showAddGradeDialog = false
            }
        )
    }

    // Dialog: Add Subject
    if (showAddSubjectDialog) {
        AddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { code, nom, coef, prof ->
                viewModel.addSubject(code, nom, coef, prof)
                showAddSubjectDialog = false
            }
        )
    }

    // Dialog: Delete Grade
    gradeToDelete?.let { grade ->
        AlertDialog(
            onDismissRequest = { gradeToDelete = null },
            title = { Text("Supprimer la note") },
            text = { Text("Supprimer la note de ${grade.noteSur20}/20 en ${grade.matiere} pour ${grade.studentNom} ?") },
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
                OutlinedButton(onClick = { gradeToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Dialog: Delete Subject
    subjectToDelete?.let { subject ->
        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            title = { Text("Supprimer la matière") },
            text = { Text("Supprimer la matière ${subject.nom} (${subject.code}) ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSubject(subject)
                        subjectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { subjectToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun StudentBulletinCard(
    student: StudentEntity,
    grades: List<GradeEntity>,
    trimestre: String,
    schoolProfile: SchoolProfile,
    onDeleteGrade: (GradeEntity) -> Unit
) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Student and calculated Average
            val totalPoints = grades.sumOf { it.noteSur20 * it.coefficient }
            val totalCoef = grades.sumOf { it.coefficient }
            val average = if (totalCoef > 0) totalPoints / totalCoef else null

            val mention = when {
                average == null -> "En attente"
                average >= 16.0 -> "Très Bien"
                average >= 14.0 -> "Bien"
                average >= 12.0 -> "Assez Bien"
                average >= 10.0 -> "Passable"
                else -> "Insuffisant"
            }

            val mentionColor = when {
                average == null -> Color.Gray
                average >= 14.0 -> EmeraldSuccess
                average >= 10.0 -> GoldAccent
                else -> MaterialTheme.colorScheme.error
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = student.nomComplet,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${student.matricule} • Classe ${student.classe} • $trimestre",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = mentionColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = if (average != null) String.format(Locale.US, "%.2f / 20", average) else "-- / 20",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = mentionColor
                        )
                        Text(
                            text = mention,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = mentionColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            if (grades.isEmpty()) {
                Text(
                    text = "Aucune note saisie pour ce trimestre.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                grades.forEach { grade ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = grade.matiere,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${grade.typeDevoir} • Coef ${grade.coefficient}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${grade.noteSur20} / 20",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (grade.noteSur20 >= 10.0) EmeraldSuccess else MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { onDeleteGrade(grade) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Supprimer note",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            val file = PdfExporter.generateBulletinPdf(
                                context = context,
                                student = student,
                                grades = grades,
                                trimestre = trimestre,
                                schoolProfile = schoolProfile
                            )
                            PdfExporter.sharePdf(
                                context = context,
                                file = file,
                                subject = "Bulletin de notes - ${student.nomComplet} ($trimestre)",
                                chooserTitle = "Partager le bulletin scolaire"
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Exporter Bulletin PDF",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddGradeDialog(
    students: List<StudentEntity>,
    subjects: List<SubjectEntity>,
    currentTrimestre: String,
    onDismiss: () -> Unit,
    onConfirm: (StudentEntity, String, String, String, Double, Int) -> Unit
) {
    var selectedStudentIndex by remember { mutableIntStateOf(0) }
    var expandedStudent by remember { mutableStateOf(false) }

    var selectedSubjectIndex by remember { mutableIntStateOf(0) }
    var expandedSubject by remember { mutableStateOf(false) }

    val trimestres = listOf("Trimestre 1", "Trimestre 2", "Trimestre 3")
    var selectedTrimestre by remember { mutableStateOf(currentTrimestre) }
    var expandedTrimestre by remember { mutableStateOf(false) }

    val types = listOf("Devoir Surveillé", "Interrogation", "Examen", "Travaux Pratiques")
    var selectedType by remember { mutableStateOf("Devoir Surveillé") }
    var expandedType by remember { mutableStateOf(false) }

    var noteText by remember { mutableStateOf("14.0") }
    var coefText by remember {
        mutableStateOf(subjects.getOrNull(0)?.coefficientDefaut?.toString() ?: "2")
    }

    val currentStudent = students.getOrNull(selectedStudentIndex)
    val currentSubject = subjects.getOrNull(selectedSubjectIndex)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Saisie d'une Note",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Student
                item {
                    if (students.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = expandedStudent,
                            onExpandedChange = { expandedStudent = it }
                        ) {
                            OutlinedTextField(
                                value = "${students[selectedStudentIndex].nomComplet} (${students[selectedStudentIndex].classe})",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Élève") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStudent) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedStudent,
                                onDismissRequest = { expandedStudent = false }
                            ) {
                                students.forEachIndexed { i, s ->
                                    DropdownMenuItem(
                                        text = { Text("${s.nomComplet} • ${s.classe}") },
                                        onClick = {
                                            selectedStudentIndex = i
                                            expandedStudent = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Subject
                item {
                    if (subjects.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = expandedSubject,
                            onExpandedChange = { expandedSubject = it }
                        ) {
                            OutlinedTextField(
                                value = subjects[selectedSubjectIndex].nom,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Matière") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubject) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedSubject,
                                onDismissRequest = { expandedSubject = false }
                            ) {
                                subjects.forEachIndexed { i, sb ->
                                    DropdownMenuItem(
                                        text = { Text("${sb.nom} (Coef ${sb.coefficientDefaut})") },
                                        onClick = {
                                            selectedSubjectIndex = i
                                            coefText = sb.coefficientDefaut.toString()
                                            expandedSubject = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Trimestre
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedTrimestre,
                        onExpandedChange = { expandedTrimestre = it }
                    ) {
                        OutlinedTextField(
                            value = selectedTrimestre,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Période") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTrimestre) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTrimestre,
                            onDismissRequest = { expandedTrimestre = false }
                        ) {
                            trimestres.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t) },
                                    onClick = {
                                        selectedTrimestre = t
                                        expandedTrimestre = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Type
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = { expandedType = it }
                    ) {
                        OutlinedTextField(
                            value = selectedType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Type d'évaluation") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false }
                        ) {
                            types.forEach { tp ->
                                DropdownMenuItem(
                                    text = { Text(tp) },
                                    onClick = {
                                        selectedType = tp
                                        expandedType = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Note /20
                item {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("Note obtenue sur 20") },
                        suffix = { Text("/ 20", fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Coefficient
                item {
                    OutlinedTextField(
                        value = coefText,
                        onValueChange = { coefText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Coefficient") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val note = noteText.replace(',', '.').toDoubleOrNull() ?: 10.0
                    val coef = coefText.toIntOrNull() ?: 2
                    if (currentStudent != null && currentSubject != null) {
                        onConfirm(currentStudent, currentSubject.nom, selectedTrimestre, selectedType, note, coef)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Enregistrer la note")
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
private fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var nom by remember { mutableStateOf("") }
    var coefText by remember { mutableStateOf("3") }
    var enseignant by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvelle Matière", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase() },
                    label = { Text("Code matière (ex: MATH, MLG, FR)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Intitulé complet de la matière") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = coefText,
                    onValueChange = { coefText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Coefficient par défaut") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = enseignant,
                    onValueChange = { enseignant = it },
                    label = { Text("Enseignant responsable (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotBlank() && nom.isNotBlank()) {
                        val coef = coefText.toIntOrNull() ?: 2
                        onConfirm(code, nom, coef, enseignant)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
