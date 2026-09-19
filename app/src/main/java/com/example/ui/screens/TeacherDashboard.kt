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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Role
import com.example.data.model.StudentEntity
import com.example.data.model.TrackingStage
import com.example.ui.components.QrScannerDialog
import com.example.ui.viewmodel.AppViewModel

@Composable
fun TeacherDashboard(
    viewModel: AppViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val selectedClass by viewModel.selectedClassFilter.collectAsState()

    var showScannerDialog by remember { mutableStateOf(false) }

    val classesList = listOf("10th-A", "8th-B", "5th-A")

    // Filter students by selected class
    val classStudents = allStudents.filter {
        if (selectedClass == "All Classes") {
            it.fullClassSection == "10th-A"
        } else {
            it.fullClassSection.equals(selectedClass, ignoreCase = true) || it.className.equals(selectedClass, ignoreCase = true)
        }
    }

    val presentCount = classStudents.count { it.currentStage == TrackingStage.ENTERED_CLASSROOM.name }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showScannerDialog = true },
                containerColor = Color(0xFF0F766E),
                contentColor = Color.White,
                modifier = Modifier.testTag("teacher_scan_fab")
            ) {
                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scan Class Entry")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(padding)
        ) {
            // Header Bar
            Surface(
                color = Color(0xFF0F766E),
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
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = currentUser?.fullName ?: "Class Teacher Portal",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Classroom Entry & Attendance Scanner",
                                color = Color(0xFFCCFBF1),
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

            // Class Selector Chips
            Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                Text(
                    text = "SELECT CLASS TO VIEW ROSTER:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(classesList) { cName ->
                        val isSelected = selectedClass == cName || (selectedClass == "All Classes" && cName == "10th-A")
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFF0F766E) else Color(0xFFE2E8F0),
                            modifier = Modifier.clickable { viewModel.selectClassFilter(cName) }
                        ) {
                            Text(
                                text = "Class $cName",
                                color = if (isSelected) Color.White else Color(0xFF334155),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Class $selectedClass Attendance",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "$presentCount of ${classStudents.size} Students in Classroom",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Button(
                            onClick = { showScannerDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan QR", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Student Roster List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(classStudents) { student ->
                    val stage = TrackingStage.fromName(student.currentStage)
                    val isInClass = stage == TrackingStage.ENTERED_CLASSROOM

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
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Roll #${student.rollNo} • ${student.studentUniqueId}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(stage.colorHex).copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = stage.titleHindi,
                                        color = Color(stage.colorHex),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            if (isInClass) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFECFDF5)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Present",
                                            tint = Color(0xFF059669),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "In Class",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF059669)
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = {
                                        viewModel.processScan(student.qrCodePayload, TrackingStage.ENTERED_CLASSROOM)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Mark In Class", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showScannerDialog) {
        QrScannerDialog(
            userRole = Role.TEACHER,
            availableStudents = classStudents,
            targetStageOverride = TrackingStage.ENTERED_CLASSROOM,
            onScanPayload = { payload, overrideStage ->
                viewModel.processScan(payload, overrideStage ?: TrackingStage.ENTERED_CLASSROOM)
            },
            onDismiss = { showScannerDialog = false }
        )
    }
}
