package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TeacherEntity
import com.example.ui.SchoolViewModel
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@Composable
fun TeachersScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val teachers by viewModel.teachers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var teacherToDelete by remember { mutableStateOf<TeacherEntity?>(null) }

    val filteredTeachers = teachers.filter { t ->
        searchQuery.isBlank() ||
        t.nomComplet.contains(searchQuery, ignoreCase = true) ||
        t.matieres.contains(searchQuery, ignoreCase = true) ||
        t.telephone.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = modifier.fillMaxSize().testTag("teachers_screen")) {
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
                            text = "Corps Professoral & Équipe Pédagogique",
                            color = Color(0xFFD1E4FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Enseignants LP3F",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${teachers.size} professeurs qualifiés • Fenoarivobe Fahasoavana",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Search
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Rechercher un professeur ou une matière...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("teacher_search_field"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            if (filteredTeachers.isEmpty()) {
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
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Aucun enseignant trouvé",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredTeachers) { teacher ->
                    TeacherCard(
                        teacher = teacher,
                        onDelete = { teacherToDelete = teacher }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }

        // Add Teacher FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = GoldAccent,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_teacher_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ajouter Enseignant", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Add Teacher Dialog
    if (showAddDialog) {
        AddTeacherDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { nom, prenoms, matieres, tel, email, classes ->
                viewModel.addTeacher(nom, prenoms, matieres, tel, email, classes)
                showAddDialog = false
            }
        )
    }

    // Delete Teacher Dialog
    teacherToDelete?.let { teacher ->
        AlertDialog(
            onDismissRequest = { teacherToDelete = null },
            title = { Text("Supprimer l'enseignant") },
            text = { Text("Supprimer ${teacher.nomComplet} (${teacher.matieres}) ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTeacher(teacher)
                        teacherToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { teacherToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun TeacherCard(
    teacher: TeacherEntity,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = NavyPrimary.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = (teacher.nom.take(1) + teacher.prenoms.take(1)).uppercase(),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = NavyPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = teacher.nomComplet,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Surface(
                            color = GoldAccent.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = teacher.matieres,
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer enseignant",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = teacher.telephone,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "Classes: ${teacher.classesAssignees}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (teacher.email.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = teacher.email,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AddTeacherDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String) -> Unit
) {
    var nom by remember { mutableStateOf("") }
    var prenoms by remember { mutableStateOf("") }
    var matieres by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var classes by remember { mutableStateOf("Toutes classes (Garderie à Tle)") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvel Enseignant", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom de famille *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = prenoms,
                    onValueChange = { prenoms = it },
                    label = { Text("Prénoms *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = matieres,
                    onValueChange = { matieres = it },
                    label = { Text("Matière(s) enseignée(s) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = telephone,
                    onValueChange = { telephone = it },
                    label = { Text("Numéro téléphone (ex: 034...) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Adresse email (optionnel)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = classes,
                    onValueChange = { classes = it },
                    label = { Text("Classes assignées") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nom.isNotBlank() && matieres.isNotBlank()) {
                        onConfirm(nom, prenoms, matieres, telephone, email, classes)
                    }
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
