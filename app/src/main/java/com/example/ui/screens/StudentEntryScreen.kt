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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.ui.SchoolViewModel
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentEntryScreen(
    viewModel: SchoolViewModel,
    onStudentCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val students by viewModel.students.collectAsState()

    // Form fields
    var nom by remember { mutableStateOf("") }
    var prenoms by remember { mutableStateOf("") }

    val nextMatriculeNumber = students.size + 1
    val defaultMatricule = "LP3F-26-${String.format(java.util.Locale.US, "%03d", nextMatriculeNumber)}"
    var matricule by remember(students.size) { mutableStateOf(defaultMatricule) }

    var selectedCycle by remember { mutableStateOf("Tous les Cycles") }
    val classes = remember(selectedCycle) {
        SchoolConstants.getClassesForCycle(selectedCycle)
    }
    var selectedClasse by remember { mutableStateOf("Garderie") }
    var expandedClasse by remember { mutableStateOf(false) }

    var sexe by remember { mutableStateOf("M") }
    var dateNaissance by remember { mutableStateOf("15/06/2020") }

    var nomTuteur by remember { mutableStateOf("") }
    var contactTuteur by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("Fenoarivobe Centre") }

    var ecolageText by remember { mutableStateOf("25000") }

    var payInscriptionFeeNow by remember { mutableStateOf(true) }
    var inscriptionFeeAmountText by remember { mutableStateOf("50000") }
    var paymentMode by remember { mutableStateOf("Espèces") }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("student_entry_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = GoldAccent.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Formulaire d'Inscription Élève",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Stockage local direct dans la base Room • LP3F",
                            color = Color(0xFFD1E4FF),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Section 1: Identification & État Civil
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1. Identification de l'Élève",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NavyPrimary
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    OutlinedTextField(
                        value = matricule,
                        onValueChange = { matricule = it },
                        label = { Text("N° Matricule Scolaire") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = nom,
                        onValueChange = { nom = it },
                        label = { Text("Nom de famille *") },
                        placeholder = { Text("ex: RAKOTOARISOA") },
                        modifier = Modifier.fillMaxWidth().testTag("student_nom_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = prenoms,
                        onValueChange = { prenoms = it },
                        label = { Text("Prénoms *") },
                        placeholder = { Text("ex: Faly Niaina") },
                        modifier = Modifier.fillMaxWidth().testTag("student_prenoms_input"),
                        singleLine = true
                    )

                    // Gender selector
                    Column {
                        Text(
                            text = "Sexe de l'élève :",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                color = if (sexe == "M") NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { sexe = "M" }
                            ) {
                                Text(
                                    text = "Garçon (Masculin)",
                                    color = if (sexe == "M") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                            Surface(
                                color = if (sexe == "F") Color(0xFFD946EF) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { sexe = "F" }
                            ) {
                                Text(
                                    text = "Fille (Féminin)",
                                    color = if (sexe == "F") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = dateNaissance,
                        onValueChange = { dateNaissance = it },
                        label = { Text("Date de naissance (JJ/MM/AAAA)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Cycle selector chips
                    Text(
                        text = "Cycle d'enseignement :",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(SchoolConstants.CYCLES) { cycle ->
                            FilterChip(
                                selected = selectedCycle == cycle,
                                onClick = {
                                    selectedCycle = cycle
                                    val available = SchoolConstants.getClassesForCycle(cycle)
                                    if (selectedClasse !in available) {
                                        selectedClasse = available.first()
                                        ecolageText = SchoolConstants.getDefaultTuitionForClass(selectedClasse).toString()
                                    }
                                },
                                label = { Text(cycle, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Classe selector
                    ExposedDropdownMenuBox(
                        expanded = expandedClasse,
                        onExpandedChange = { expandedClasse = it }
                    ) {
                        OutlinedTextField(
                            value = "$selectedClasse (${SchoolConstants.getCycleForClass(selectedClasse)})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Classe d'admission *") },
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
                                            Surface(
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = SchoolConstants.getCycleForClass(cl),
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
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
            }
        }

        // Section 2: Coordonnées Tuteur
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. Tuteur & Contact Parent",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NavyPrimary
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    OutlinedTextField(
                        value = nomTuteur,
                        onValueChange = { nomTuteur = it },
                        label = { Text("Nom complet du Tuteur / Parent *") },
                        placeholder = { Text("ex: M. Rabe Justin") },
                        modifier = Modifier.fillMaxWidth().testTag("student_tuteur_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = contactTuteur,
                        onValueChange = { contactTuteur = it },
                        label = { Text("Téléphone Tuteur (ex: 034 12 345 67) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("student_contact_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = adresse,
                        onValueChange = { adresse = it },
                        label = { Text("Adresse de résidence") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }

        // Section 3: Écolage & Frais (Ariary - Ar)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3. Écolage Mensuel (Ariary - Ar)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NavyPrimary
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    OutlinedTextField(
                        value = ecolageText,
                        onValueChange = { ecolageText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Tarif mensuel de scolarité") },
                        suffix = { Text("Ar", fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Quick suggestions for Ariary
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val suggestions = listOf("35000", "40000", "45000", "50000")
                        items(suggestions) { sugg ->
                            Surface(
                                color = if (ecolageText == sugg) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { ecolageText = sugg }
                            ) {
                                Text(
                                    text = "${viewModel.formatAriary(sugg.toLong())}/mois",
                                    color = if (ecolageText == sugg) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Optional immediate enrollment fee payment
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { payInscriptionFeeNow = !payInscriptionFeeNow }
                    ) {
                        Checkbox(
                            checked = payInscriptionFeeNow,
                            onCheckedChange = { payInscriptionFeeNow = it },
                            colors = CheckboxDefaults.colors(checkedColor = GoldAccent)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Encaisser les droits d'inscription maintenant",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Crée un versement d'inscription dans la base avec reçu",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (payInscriptionFeeNow) {
                        OutlinedTextField(
                            value = inscriptionFeeAmountText,
                            onValueChange = { inscriptionFeeAmountText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Montant Inscription en Ariary") },
                            suffix = { Text("Ar", fontWeight = FontWeight.Bold) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Error message if any
        if (errorMessage != null) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    if (nom.isBlank()) {
                        errorMessage = "Veuillez renseigner le nom de famille de l'élève."
                        return@Button
                    }
                    if (prenoms.isBlank()) {
                        errorMessage = "Veuillez renseigner le prénom de l'élève."
                        return@Button
                    }
                    if (nomTuteur.isBlank()) {
                        errorMessage = "Veuillez renseigner le nom du tuteur."
                        return@Button
                    }
                    if (contactTuteur.isBlank()) {
                        errorMessage = "Veuillez renseigner le contact téléphonique du tuteur."
                        return@Button
                    }

                    errorMessage = null
                    val ecolageAr = ecolageText.toLongOrNull() ?: 35000L

                    // Insert student into Room DB
                    viewModel.addStudent(
                        nom = nom.trim().uppercase(),
                        prenoms = prenoms.trim(),
                        classe = selectedClasse,
                        sexe = sexe,
                        dateNaissance = dateNaissance.trim(),
                        nomTuteur = nomTuteur.trim(),
                        contactTuteur = contactTuteur.trim(),
                        adresse = adresse.trim(),
                        ecolageMensuelAr = ecolageAr
                    )

                    // If immediate enrollment fee is checked, record fee payment
                    if (payInscriptionFeeNow) {
                        val feeAmount = inscriptionFeeAmountText.toLongOrNull() ?: 50000L
                        val newStudent = com.example.data.StudentEntity(
                            id = 0,
                            matricule = matricule,
                            nom = nom.trim().uppercase(),
                            prenoms = prenoms.trim(),
                            classe = selectedClasse,
                            sexe = sexe,
                            dateNaissance = dateNaissance,
                            nomTuteur = nomTuteur,
                            contactTuteur = contactTuteur,
                            adresse = adresse,
                            ecolageMensuelAr = ecolageAr
                        )
                        viewModel.recordFeePayment(
                            student = newStudent,
                            mois = "Inscription",
                            montantAr = feeAmount,
                            modePaiement = paymentMode,
                            remarques = "Frais d'inscription et dossier scolaire"
                        )
                    }

                    showSuccessDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_student_button")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Enregistrer l'Élève dans la Base Room",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Success confirmation dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onStudentCreated()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Inscription Confirmée !", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("L'élève $nom $prenoms a été inscrit(e) avec succès en classe de $selectedClasse.")
                    Text("Matricule attribué : $matricule", fontWeight = FontWeight.SemiBold)
                    Text("Écolage mensuel : ${viewModel.formatAriary(ecolageText.toLongOrNull() ?: 35000L)}")
                    if (payInscriptionFeeNow) {
                        Text(
                            "Droits d'inscription de ${viewModel.formatAriary(inscriptionFeeAmountText.toLongOrNull() ?: 50000L)} enregistrés.",
                            color = EmeraldSuccess,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text("Les données sont immédiatement stockées en local dans Room.", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onStudentCreated()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Voir le registre")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        // Reset form for next entry
                        nom = ""
                        prenoms = ""
                        nomTuteur = ""
                        contactTuteur = ""
                    }
                ) {
                    Text("Inscrire un autre élève")
                }
            }
        )
    }
}
