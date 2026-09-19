package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Role
import com.example.data.model.StudentEntity
import com.example.data.model.TrackingStage
import com.example.ui.components.QrScannerDialog
import com.example.ui.components.StudentIdCard
import com.example.ui.components.StudentJourneyStepper
import com.example.ui.components.VoiceAlertDialog
import com.example.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ParentDashboard(
    viewModel: AppViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentSchool by viewModel.currentSchool.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val recentLogs by viewModel.recentLogs.collectAsState()
    val activeParentAlert by viewModel.activeParentAlert.collectAsState()

    var showScannerDialog by remember { mutableStateOf(false) }
    var overrideTargetStage by remember { mutableStateOf<TrackingStage?>(null) }

    // Find linked student or default to first student for demo
    val student: StudentEntity? = allStudents.find { it.id == currentUser?.linkedStudentId }
        ?: allStudents.firstOrNull()

    val currentStage = if (student != null) TrackingStage.fromName(student.currentStage) else TrackingStage.AT_HOME
    val studentLogs = recentLogs.filter { it.studentId == (student?.id ?: 0L) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Header Bar
        Surface(
            color = Color(0xFF1E3A8A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FamilyRestroom,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = currentUser?.fullName ?: "Parent Portal",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Live Child Tracking & Voice Alerts",
                            color = Color(0xFF93C5FD),
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.testTag("logout_button")
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                }
            }
        }

        if (student == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No student linked to this account.", color = Color(0xFF64748B))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp)
            ) {
                // Stepper Widget
                StudentJourneyStepper(currentStage = currentStage)

                Spacer(modifier = Modifier.height(14.dp))

                // Parent Scan Actions Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "HOME SCAN CONTROLS:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 1. Departed Home Scan
                            Button(
                                onClick = {
                                    overrideTargetStage = TrackingStage.DEPARTED_HOME
                                    viewModel.processScan(student.qrCodePayload, TrackingStage.DEPARTED_HOME)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("parent_scan_depart_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = Icons.Default.DirectionsWalk, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Ghar Se Nikla", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("(Departed)", fontSize = 9.sp, color = Color(0xFFBFDBFE))
                                }
                            }

                            // 2. Reached Home Scan
                            Button(
                                onClick = {
                                    overrideTargetStage = TrackingStage.REACHED_HOME
                                    viewModel.processScan(student.qrCodePayload, TrackingStage.REACHED_HOME)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("parent_scan_reached_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Ghar Pahuch Gya", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("(Reached Safely)", fontSize = 9.sp, color = Color(0xFFA7F3D0))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Student Identity Card
                Text(
                    text = "STUDENT IDENTITY CARD & QR:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(6.dp))

                StudentIdCard(
                    student = student,
                    school = currentSchool
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Activity Logs for this child
                Text(
                    text = "TODAY'S ACTIVITY LOGS:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (studentLogs.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Abhi koi journey log record nahi hui hai.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    studentLogs.forEach { log ->
                        val stage = TrackingStage.fromName(log.stage)
                        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(stage.colorHex),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = stage.titleHindi,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "Scanned by: ${log.scannedByName} (${log.scannedByRole})",
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Text(
                                    text = sdf.format(Date(log.timestamp)),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF334155)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Continuous ringing voice notification modal for parent!
    activeParentAlert?.let { alert ->
        VoiceAlertDialog(
            alert = alert,
            onAcknowledge = {
                viewModel.dismissVoiceAlert()
            }
        )
    }

    if (showScannerDialog && student != null) {
        QrScannerDialog(
            userRole = Role.PARENT,
            availableStudents = listOf(student),
            targetStageOverride = overrideTargetStage,
            onScanPayload = { payload, stage ->
                viewModel.processScan(payload, stage ?: overrideTargetStage)
            },
            onDismiss = { showScannerDialog = false }
        )
    }
}
