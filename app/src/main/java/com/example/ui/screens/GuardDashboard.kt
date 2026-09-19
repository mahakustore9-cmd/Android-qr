package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Role
import com.example.data.model.TrackingStage
import com.example.ui.components.QrScannerDialog
import com.example.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GuardDashboard(
    viewModel: AppViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val recentLogs by viewModel.recentLogs.collectAsState()

    var isExitMode by remember { mutableStateOf(false) } // false = Entry Scan, true = Exit Scan
    var showScannerDialog by remember { mutableStateOf(false) }

    val activeStage = if (isExitMode) TrackingStage.LEFT_SCHOOL_GATE else TrackingStage.REACHED_SCHOOL_GATE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Header
        Surface(
            color = Color(0xFFB45309), // Amber Guard color
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
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = currentUser?.fullName ?: "Main Gate Security",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Campus Entry & Exit Checkpoint",
                            color = Color(0xFFFEF3C7),
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

        // Mode Switcher Banner (Entry vs Exit)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SELECT SCANNING MODE:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Entry Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (!isExitMode) Color(0xFF2563EB) else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isExitMode = false }
                            .testTag("guard_entry_mode_btn")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ENTRY SCAN ➔",
                                color = if (!isExitMode) Color.White else Color(0xFF64748B),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "School Pahuch Gya",
                                color = if (!isExitMode) Color(0xFFBFDBFE) else Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Exit Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isExitMode) Color(0xFFEA580C) else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isExitMode = true }
                            .testTag("guard_exit_mode_btn")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "EXIT SCAN ➔",
                                color = if (isExitMode) Color.White else Color(0xFF64748B),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "School Se Nikal Chuka",
                                color = if (isExitMode) Color(0xFFFED7AA) else Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Big Scan Action Button
                Button(
                    onClick = { showScannerDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("guard_open_scanner_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isExitMode) Color(0xFFEA580C) else Color(0xFF2563EB)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isExitMode) "SCAN FOR GATE EXIT (NIKLE)" else "SCAN FOR GATE ENTRY (PAHUCH GYE)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Quick 1-Tap Student Action List
        Text(
            text = "FAST 1-TAP GATE SCANNING (${if (isExitMode) "EXIT" else "ENTRY"}):",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allStudents) { student ->
                val currentStage = TrackingStage.fromName(student.currentStage)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = student.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Roll #${student.rollNo} • ${student.fullClassSection}",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(currentStage.colorHex).copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "Status: ${currentStage.titleHindi}",
                                    color = Color(currentStage.colorHex),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.processScan(student.qrCodePayload, activeStage)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isExitMode) Color(0xFFEA580C) else Color(0xFF2563EB)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("gate_quick_scan_${student.rollNo}")
                        ) {
                            Text(
                                text = if (isExitMode) "Mark Exit" else "Mark In",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showScannerDialog) {
        QrScannerDialog(
            userRole = Role.GATE_GUARD,
            availableStudents = allStudents,
            targetStageOverride = activeStage,
            onScanPayload = { payload, stage ->
                viewModel.processScan(payload, stage ?: activeStage)
            },
            onDismiss = { showScannerDialog = false }
        )
    }
}
