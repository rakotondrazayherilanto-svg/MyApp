package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.SchoolViewModel
import com.example.ui.components.AttendanceHistoryComponent
import com.example.ui.components.MarkAttendanceBySubjectComponent
import com.example.ui.components.StudentAttendanceDossierComponent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@Composable
fun AttendanceScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val attendances by viewModel.attendances.collectAsState()
    val students by viewModel.students.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val teachers by viewModel.teachers.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val todayDate = remember { viewModel.getCurrentDate() }

    val todayAttendances = remember(attendances, todayDate) {
        attendances.filter { it.dateJour == todayDate }
    }
    val todayPresences = todayAttendances.count { it.statut == "Présent" }
    val todayAbsences = todayAttendances.count { it.statut == "Absent" }
    val todayRate = if (todayAttendances.isNotEmpty()) (todayPresences * 100) / todayAttendances.size else 100

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("attendance_screen")
    ) {
        // Header Banner
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
                            text = "Assiduité & Contrôle de Présence",
                            color = Color(0xFFD1E4FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Feuille d'Appel LP3F",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$todayRate%",
                                color = GoldAccent,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Taux du jour",
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Aujourd'hui : $todayDate • Fenoarivobe Fahasoavana",
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation Tabs (3 distinct perspectives)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HowToReg, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pointer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Registre (${attendances.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dossiers", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Contents
        when (selectedTab) {
            0 -> MarkAttendanceBySubjectComponent(
                viewModel = viewModel,
                students = students,
                subjects = subjects,
                teachers = teachers,
                attendances = attendances
            )
            1 -> AttendanceHistoryComponent(
                viewModel = viewModel,
                attendances = attendances,
                subjects = subjects
            )
            2 -> StudentAttendanceDossierComponent(
                students = students,
                attendances = attendances
            )
        }
    }
}
