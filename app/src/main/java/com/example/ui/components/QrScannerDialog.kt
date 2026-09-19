package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.Role
import com.example.data.model.StudentEntity
import com.example.data.model.TrackingStage

@Composable
fun QrScannerDialog(
    userRole: Role,
    availableStudents: List<StudentEntity>,
    targetStageOverride: TrackingStage? = null,
    onScanPayload: (String, TrackingStage?) -> Unit,
    onDismiss: () -> Unit
) {
    var manualInput by remember { mutableStateOf("") }
    var isFlashOn by remember { mutableStateOf(false) }

    // Laser Animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("qr_scanner_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scanner",
                            tint = Color(0xFF1E3A8A),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "QR Journey Scanner",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Role: ${userRole.displayName}",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_scanner_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scanner Viewfinder Area
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isFlashOn) Color(0xFF1E293B) else Color(0xFF090D16))
                        .border(2.dp, Color(0xFF3B82F6), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Reticle Corners
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .border(2.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    )

                    // Laser Line
                    Box(
                        modifier = Modifier
                            .width(170.dp)
                            .height(3.dp)
                            .offset(y = (laserPosition - 90).dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color(0xFF10B981), Color(0xFF34D399), Color.Transparent)
                                )
                            )
                    )

                    // Flashlight toggle button
                    IconButton(
                        onClick = { isFlashOn = !isFlashOn },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Flashlight",
                            tint = if (isFlashOn) Color(0xFFFACC15) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "Point Camera at Student QR",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fast Emulator/Demo Quick-Scan Chips
                Text(
                    text = "Quick Demo Scan (Select Student):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableStudents) { student ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEFF6FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier
                                .clickable {
                                    onScanPayload(student.qrCodePayload, targetStageOverride)
                                    onDismiss()
                                }
                                .testTag("quick_scan_${student.rollNo}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = Color(0xFF1E3A8A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = student.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E3A8A)
                                    )
                                    Text(
                                        text = "${student.fullClassSection} • #${student.rollNo}",
                                        fontSize = 9.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Manual Input
                OutlinedTextField(
                    value = manualInput,
                    onValueChange = { manualInput = it },
                    label = { Text("Or enter Student ID / QR Code") },
                    placeholder = { Text("e.g. STU-DPGA-2025-101-AARAV") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_qr_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (manualInput.isNotBlank()) {
                                onScanPayload(manualInput.trim(), targetStageOverride)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("submit_scan_button")
                    ) {
                        Text("Submit Scan")
                    }
                }
            }
        }
    }
}
