package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SchoolViewModel
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary
import com.example.utils.DataSyncSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SendDataToOwnerDialog(
    viewModel: SchoolViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val settings by viewModel.appSettings.collectAsState()
    val licenseState by viewModel.licenseState.collectAsState()

    val students by viewModel.students.collectAsState()
    val teachers by viewModel.teachers.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val grades by viewModel.grades.collectAsState()
    val fees by viewModel.fees.collectAsState()
    val attendances by viewModel.attendances.collectAsState()

    var isPreparing by remember { mutableStateOf(false) }

    val totalRecettesAr = remember(fees) { fees.sumOf { it.montantAr } }
    val symbols = remember { DecimalFormatSymbols(Locale.FRENCH).apply { groupingSeparator = ' ' } }
    val currencyFormat = remember { DecimalFormat("#,###", symbols) }

    val recipientEmail = settings.schoolProfile.ownerEmail

    AlertDialog(
        onDismissRequest = { if (!isPreparing) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = NavyPrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Transmission des Données",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = NavyPrimary
                    )
                    Text(
                        text = "Sauvegarde vers la Direction",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info Box sur le choix libre de l'utilisateur
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF1D4ED8),
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Vous avez le libre choix d'envoyer ou non vos données. Cette action transmet les informations saisies dans l'application au responsable pour centralisation ou sauvegarde.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = Color(0xFF1E3A8A)
                        )
                    }
                }

                // Destinataire officiel
                Column {
                    Text(
                        text = "DESTINATAIRE :",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = NavyPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = recipientEmail,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = NavyPrimary
                            )
                        }
                    }
                }

                // Résumé des données prêtes à être envoyées
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Aperçu des informations à transmettre :",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• Fiches élèves :", fontSize = 12.sp, color = Color.Gray)
                            Text("${students.size} inscrits", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• Écolages encaissés :", fontSize = 12.sp, color = Color.Gray)
                            Text("${currencyFormat.format(totalRecettesAr)} Ar", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldSuccess)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• Enseignants :", fontSize = 12.sp, color = Color.Gray)
                            Text("${teachers.size} professeurs", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• Notes & Évaluations :", fontSize = 12.sp, color = Color.Gray)
                            Text("${grades.size} saisies", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• Présences & Emplois du temps :", fontSize = 12.sp, color = Color.Gray)
                            Text("${attendances.size + schedules.size} entrées", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                if (isPreparing) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = NavyPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Génération de l'archive de sauvegarde...", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Option 1 : Envoyer directement par Email au propriétaire
                Button(
                    onClick = {
                        isPreparing = true
                        coroutineScope.launch {
                            val jsonContent = withContext(Dispatchers.Default) {
                                DataSyncSender.generateFullBackupJson(
                                    profile = settings.schoolProfile,
                                    deviceId = licenseState.installationId,
                                    productKey = licenseState.productKey,
                                    students = students,
                                    teachers = teachers,
                                    subjects = subjects,
                                    schedules = schedules,
                                    grades = grades,
                                    fees = fees,
                                    attendances = attendances
                                )
                            }
                            val summary = withContext(Dispatchers.Default) {
                                DataSyncSender.generateHumanReadableSummary(
                                    profile = settings.schoolProfile,
                                    deviceId = licenseState.installationId,
                                    productKey = licenseState.productKey,
                                    students = students,
                                    teachers = teachers,
                                    subjects = subjects,
                                    schedules = schedules,
                                    grades = grades,
                                    fees = fees,
                                    attendances = attendances
                                )
                            }
                            val file = withContext(Dispatchers.IO) {
                                DataSyncSender.saveJsonToCacheFile(context, jsonContent)
                            }
                            isPreparing = false

                            val subject = "[LP3F] Sauvegarde Données Scolaires - ${settings.schoolProfile.schoolAcronym} - ${SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(Date())}"
                            DataSyncSender.sendEmailToOwner(
                                context = context,
                                ownerEmail = recipientEmail,
                                subject = subject,
                                bodyText = summary,
                                jsonFile = file
                            )
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("send_data_email_button"),
                    enabled = !isPreparing
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Envoyer par E-mail à la Direction", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Option 2 : Partager via WhatsApp / Telegram / Drive
                OutlinedButton(
                    onClick = {
                        isPreparing = true
                        coroutineScope.launch {
                            val jsonContent = withContext(Dispatchers.Default) {
                                DataSyncSender.generateFullBackupJson(
                                    profile = settings.schoolProfile,
                                    deviceId = licenseState.installationId,
                                    productKey = licenseState.productKey,
                                    students = students,
                                    teachers = teachers,
                                    subjects = subjects,
                                    schedules = schedules,
                                    grades = grades,
                                    fees = fees,
                                    attendances = attendances
                                )
                            }
                            val summary = withContext(Dispatchers.Default) {
                                DataSyncSender.generateHumanReadableSummary(
                                    profile = settings.schoolProfile,
                                    deviceId = licenseState.installationId,
                                    productKey = licenseState.productKey,
                                    students = students,
                                    teachers = teachers,
                                    subjects = subjects,
                                    schedules = schedules,
                                    grades = grades,
                                    fees = fees,
                                    attendances = attendances
                                )
                            }
                            val file = withContext(Dispatchers.IO) {
                                DataSyncSender.saveJsonToCacheFile(context, jsonContent)
                            }
                            isPreparing = false

                            val subject = "[LP3F] Données Scolaires - ${settings.schoolProfile.schoolAcronym}"
                            DataSyncSender.shareViaAnyApp(
                                context = context,
                                subject = subject,
                                bodyText = summary,
                                jsonFile = file
                            )
                            onDismiss()
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isPreparing
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Partager (WhatsApp, Drive, Autre)", fontSize = 12.sp)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isPreparing
            ) {
                Text("Ne pas envoyer (Garder en local)", color = Color.Gray, fontSize = 12.sp)
            }
        }
    )
}
