package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FeePaymentEntity
import com.example.data.SchoolProfile
import com.example.data.StudentEntity
import com.example.ui.SchoolViewModel
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary
import com.example.utils.PdfExporter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeesScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.appSettings.collectAsState()
    val schoolProfile = settings.schoolProfile
    val fees by viewModel.fees.collectAsState()
    val students by viewModel.students.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Historique des paiements, 1: État par élève
    var selectedMonthFilter by remember { mutableStateOf("Tous") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFeeForReceipt by remember { mutableStateOf<FeePaymentEntity?>(null) }
    var feeToDelete by remember { mutableStateOf<FeePaymentEntity?>(null) }

    val monthsList = listOf(
        "Tous", "Inscription", "Septembre", "Octobre", "Novembre",
        "Décembre", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet"
    )

    val totalRecettesAr = fees.sumOf { it.montantAr }

    val filteredFees = fees.filter { fee ->
        (selectedMonthFilter == "Tous" || fee.mois.equals(selectedMonthFilter, ignoreCase = true)) &&
        (searchQuery.isBlank() ||
         fee.studentNom.contains(searchQuery, ignoreCase = true) ||
         fee.studentMatricule.contains(searchQuery, ignoreCase = true) ||
         fee.referenceRecu.contains(searchQuery, ignoreCase = true))
    }

    Box(modifier = modifier.fillMaxSize().testTag("fees_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header summary card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Gestion des Frais & Écolages",
                            color = Color(0xFFD1E4FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = viewModel.formatAriary(totalRecettesAr),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Unité monétaire: ARIARY (Ar) • LP3F Fenoarivobe",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Tabs: Historique vs État par élève
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Versements (${fees.size})", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Situation Élèves", fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            if (selectedTab == 0) {
                // Search & Filter
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().testTag("fee_search_field"),
                        placeholder = { Text("Rechercher par élève, matricule, reçu...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                // Month chips filter
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(monthsList) { month ->
                            val isSelected = selectedMonthFilter == month
                            Surface(
                                color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { selectedMonthFilter = month }
                            ) {
                                Text(
                                    text = month,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                if (filteredFees.isEmpty()) {
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
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Aucun paiement correspondant",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    items(filteredFees) { fee ->
                        FeePaymentCard(
                            fee = fee,
                            formattedAmount = viewModel.formatAriary(fee.montantAr),
                            onViewReceipt = { selectedFeeForReceipt = fee },
                            onShareReceiptPdf = {
                                val file = PdfExporter.generateReceiptPdf(
                                    context = context,
                                    fee = fee,
                                    schoolProfile = schoolProfile
                                )
                                PdfExporter.sharePdf(
                                    context = context,
                                    file = file,
                                    subject = "Reçu d'écolage - ${fee.studentNom} (${fee.referenceRecu})",
                                    chooserTitle = "Partager le reçu officiel"
                                )
                            },
                            onDelete = { feeToDelete = fee }
                        )
                    }
                }
            } else {
                // Situation par élève (Mois payés vs impayés)
                items(students) { student ->
                    val studentFees = fees.filter { it.studentId == student.id }
                    val totalPaidAr = studentFees.sumOf { it.montantAr }
                    val paidMonths = studentFees.map { it.mois }

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
                                Column {
                                    Text(
                                        text = student.nomComplet,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "${student.matricule} • Classe ${student.classe}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${viewModel.formatAriary(student.ecolageMensuelAr)}/mois",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total versé: ${viewModel.formatAriary(totalPaidAr)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldSuccess
                                )
                                Text(
                                    text = "${studentFees.size} mois réglés",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            // Chips of paid months
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(listOf("Inscription", "Septembre", "Octobre", "Novembre", "Décembre", "Janvier")) { m ->
                                    val isPaid = paidMonths.contains(m)
                                    Surface(
                                        color = if (isPaid) EmeraldSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "$m ${if (isPaid) "✓" else "✕"}",
                                            color = if (isPaid) EmeraldSuccess else MaterialTheme.colorScheme.error,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }

        // FAB to record new payment
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = GoldAccent,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_fee_payment_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Encaisser", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Dialog: Enregistrer un paiement
    if (showAddDialog) {
        AddFeePaymentDialog(
            students = students,
            formatAriary = { viewModel.formatAriary(it) },
            onDismiss = { showAddDialog = false },
            onConfirm = { student, mois, montantAr, mode, remarques ->
                viewModel.recordFeePayment(student, mois, montantAr, mode, remarques)
                showAddDialog = false
            }
        )
    }

    // Dialog: Reçu officiel
    selectedFeeForReceipt?.let { fee ->
        OfficialReceiptDialog(
            fee = fee,
            schoolProfile = schoolProfile,
            formatAriary = { viewModel.formatAriary(it) },
            onDismiss = { selectedFeeForReceipt = null }
        )
    }

    // Dialog: Confirmation suppression
    feeToDelete?.let { fee ->
        AlertDialog(
            onDismissRequest = { feeToDelete = null },
            title = { Text("Supprimer le versement") },
            text = { Text("Voulez-vous supprimer le versement de ${viewModel.formatAriary(fee.montantAr)} pour ${fee.studentNom} (${fee.referenceRecu}) ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteFeePayment(fee)
                        feeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { feeToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun FeePaymentCard(
    fee: FeePaymentEntity,
    formattedAmount: String,
    onViewReceipt: () -> Unit,
    onShareReceiptPdf: () -> Unit,
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
                        color = GoldAccent.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = fee.studentNom,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${fee.studentMatricule} • Classe ${fee.classe}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = formattedAmount,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = EmeraldSuccess
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mois: ${fee.mois} • ${fee.modePaiement}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${fee.referenceRecu} • ${fee.datePaiement}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(onClick = onShareReceiptPdf) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Partager PDF",
                            tint = GoldAccent
                        )
                    }
                    IconButton(onClick = onViewReceipt) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = "Voir le reçu",
                            tint = NavyPrimary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFeePaymentDialog(
    students: List<StudentEntity>,
    formatAriary: (Long) -> String,
    onDismiss: () -> Unit,
    onConfirm: (StudentEntity, String, Long, String, String) -> Unit
) {
    var selectedStudentIndex by remember { mutableIntStateOf(0) }
    var expandedStudentDropdown by remember { mutableStateOf(false) }

    val months = listOf(
        "Inscription", "Septembre", "Octobre", "Novembre",
        "Décembre", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet"
    )
    var selectedMonth by remember { mutableStateOf("Septembre") }
    var expandedMonthDropdown by remember { mutableStateOf(false) }

    val currentStudent = students.getOrNull(selectedStudentIndex)
    var amountText by remember(currentStudent) {
        mutableStateOf(currentStudent?.ecolageMensuelAr?.toString() ?: "35000")
    }

    val modes = listOf("Espèces", "MVola", "Orange Money", "Airtel Money", "Virement bancaire")
    var selectedMode by remember { mutableStateOf("Espèces") }
    var expandedModeDropdown by remember { mutableStateOf(false) }

    var remarks by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Encaisser un Écolage (Ar)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Student selector
                if (students.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = expandedStudentDropdown,
                        onExpandedChange = { expandedStudentDropdown = it }
                    ) {
                        OutlinedTextField(
                            value = "${students[selectedStudentIndex].nomComplet} (${students[selectedStudentIndex].classe})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Élève") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStudentDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedStudentDropdown,
                            onDismissRequest = { expandedStudentDropdown = false }
                        ) {
                            students.forEachIndexed { index, st ->
                                DropdownMenuItem(
                                    text = { Text("${st.nomComplet} • ${st.classe} (${st.matricule})") },
                                    onClick = {
                                        selectedStudentIndex = index
                                        amountText = st.ecolageMensuelAr.toString()
                                        expandedStudentDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Month selector
                ExposedDropdownMenuBox(
                    expanded = expandedMonthDropdown,
                    onExpandedChange = { expandedMonthDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedMonth,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mois / Motif") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMonthDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMonthDropdown,
                        onDismissRequest = { expandedMonthDropdown = false }
                    ) {
                        months.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m) },
                                onClick = {
                                    selectedMonth = m
                                    if (m == "Inscription") {
                                        amountText = "50000"
                                    } else {
                                        amountText = currentStudent?.ecolageMensuelAr?.toString() ?: "35000"
                                    }
                                    expandedMonthDropdown = false
                                }
                            )
                        }
                    }
                }

                // Amount in Ariary (Ar)
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Montant en Ariary (Ar)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    suffix = { Text("Ar", fontWeight = FontWeight.Bold) }
                )

                // Mode selector
                ExposedDropdownMenuBox(
                    expanded = expandedModeDropdown,
                    onExpandedChange = { expandedModeDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedMode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mode de règlement") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedModeDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedModeDropdown,
                        onDismissRequest = { expandedModeDropdown = false }
                    ) {
                        modes.forEach { md ->
                            DropdownMenuItem(
                                text = { Text(md) },
                                onClick = {
                                    selectedMode = md
                                    expandedModeDropdown = false
                                }
                            )
                        }
                    }
                }

                // Remarks
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarque / N° transaction (optionnel)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    currentStudent?.let { st ->
                        val amount = amountText.toLongOrNull() ?: st.ecolageMensuelAr
                        onConfirm(st, selectedMonth, amount, selectedMode, remarks)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Valider le paiement", color = Color.White)
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
private fun OfficialReceiptDialog(
    fee: FeePaymentEntity,
    schoolProfile: SchoolProfile,
    formatAriary: (Long) -> String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Fermer")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    val file = PdfExporter.generateReceiptPdf(
                        context = context,
                        fee = fee,
                        schoolProfile = schoolProfile
                    )
                    PdfExporter.sharePdf(
                        context = context,
                        file = file,
                        subject = "Reçu d'écolage - ${fee.studentNom} (${fee.referenceRecu})",
                        chooserTitle = "Partager le reçu officiel"
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Partager PDF")
            }
        },
        text = {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFDFE)),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // School header
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = schoolProfile.schoolName.uppercase(),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = NavyPrimary
                        )
                        Text(
                            text = "${schoolProfile.schoolMotto} (${schoolProfile.schoolAcronym})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = GoldAccent
                        )
                        Text(
                            text = "${schoolProfile.schoolAddress} • Tél: ${schoolProfile.schoolPhone}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(thickness = 2.dp, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "REÇU DE SCOLARITÉ N° ${fee.referenceRecu}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NavyPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Date : ${fee.datePaiement}", fontSize = 12.sp)
                    Text(text = "Élève : ${fee.studentNom}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Matricule : ${fee.studentMatricule}   |   Classe : ${fee.classe}", fontSize = 12.sp)
                    Text(text = "Motif : Écolage mois de ${fee.mois}", fontSize = 12.sp)
                    Text(text = "Mode de règlement : ${fee.modePaiement}", fontSize = 12.sp)
                    if (fee.remarques.isNotBlank()) {
                        Text(text = "Note : ${fee.remarques}", fontSize = 11.sp, color = Color.DarkGray)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = EmeraldSuccess.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MONTANT VERSÉ :",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = EmeraldSuccess
                            )
                            Text(
                                text = formatAriary(fee.montantAr),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = EmeraldSuccess
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cachet de l'Établissement",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = schoolProfile.cashierTitle,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NavyPrimary
                        )
                    }
                }
            }
        }
    )
}
