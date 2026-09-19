package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentEntity
import com.example.data.model.TrackingLogEntity
import com.example.data.model.TrackingStage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 5-Step Visual Journey Stepper for single student (e.g. Parent & Detail view)
 */
@Composable
fun StudentJourneyStepper(
    currentStage: TrackingStage,
    modifier: Modifier = Modifier
) {
    val stages = listOf(
        TrackingStage.DEPARTED_HOME,
        TrackingStage.REACHED_SCHOOL_GATE,
        TrackingStage.ENTERED_CLASSROOM,
        TrackingStage.LEFT_SCHOOL_GATE,
        TrackingStage.REACHED_HOME
    )

    val currentStepOrder = currentStage.stepOrder

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE JOURNEY TRACKING",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(currentStage.colorHex).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = currentStage.titleHindi,
                        color = Color(currentStage.colorHex),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Horizontal Stepper Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                stages.forEachIndexed { index, stage ->
                    val isCompleted = stage.stepOrder < currentStepOrder
                    val isCurrent = stage.stepOrder == currentStepOrder
                    val isPending = stage.stepOrder > currentStepOrder

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> Color(0xFF10B981)
                                        isCurrent -> Color(stage.colorHex)
                                        else -> Color(0xFFE2E8F0)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = getStageIcon(stage),
                                    contentDescription = stage.name,
                                    tint = if (isCurrent) Color.White else Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = stage.titleHindi.split(" ").take(2).joinToString(" "),
                            fontSize = 9.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) Color(stage.colorHex) else Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }

                    if (index < stages.size - 1) {
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .weight(0.5f)
                                .background(
                                    if (stage.stepOrder < currentStepOrder) Color(0xFF10B981) else Color(0xFFE2E8F0)
                                )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Class-wise Comprehensive Matrix Table (for Admin and Teachers)
 */
@Composable
fun ClassJourneyMatrixTable(
    students: List<StudentEntity>,
    logs: List<TrackingLogEntity>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Title Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STUDENT TRACKING MATRIX",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "${students.size} Students Enrolled",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }

            // Scrollable Matrix Table
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                Column {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFE2E8F0))
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MatrixHeaderCell("Roll & Name", 140.dp)
                        MatrixHeaderCell("Class", 65.dp)
                        MatrixHeaderCell("1. Ghar Se Out", 100.dp)
                        MatrixHeaderCell("2. Gate In", 95.dp)
                        MatrixHeaderCell("3. Class In", 95.dp)
                        MatrixHeaderCell("4. Gate Out", 95.dp)
                        MatrixHeaderCell("5. Ghar In", 95.dp)
                        MatrixHeaderCell("Live Status", 130.dp)
                    }

                    Divider(color = Color(0xFFCBD5E1), thickness = 1.dp)

                    if (students.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Is class me koi student nahi hai.",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        students.forEachIndexed { index, student ->
                            val currentStage = TrackingStage.fromName(student.currentStage)
                            val studentLogs = logs.filter { it.studentId == student.id }

                            val time1 = findStageTime(studentLogs, TrackingStage.DEPARTED_HOME)
                            val time2 = findStageTime(studentLogs, TrackingStage.REACHED_SCHOOL_GATE)
                            val time3 = findStageTime(studentLogs, TrackingStage.ENTERED_CLASSROOM)
                            val time4 = findStageTime(studentLogs, TrackingStage.LEFT_SCHOOL_GATE)
                            val time5 = findStageTime(studentLogs, TrackingStage.REACHED_HOME)

                            Row(
                                modifier = Modifier
                                    .background(if (index % 2 == 0) Color.White else Color(0xFFF8FAFC))
                                    .padding(vertical = 8.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Student Info
                                Column(modifier = Modifier.width(140.dp)) {
                                    Text(
                                        text = student.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Roll #${student.rollNo}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                // Class
                                Box(modifier = Modifier.width(65.dp)) {
                                    Text(
                                        text = student.fullClassSection,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF334155)
                                    )
                                }

                                // 5 Stages Cells
                                MatrixStageCell(time1, currentStage.stepOrder >= 1, 100.dp)
                                MatrixStageCell(time2, currentStage.stepOrder >= 2, 95.dp)
                                MatrixStageCell(time3, currentStage.stepOrder >= 3, 95.dp)
                                MatrixStageCell(time4, currentStage.stepOrder >= 4, 95.dp)
                                MatrixStageCell(time5, currentStage.stepOrder >= 5, 95.dp)

                                // Live Status Badge
                                Box(modifier = Modifier.width(130.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(currentStage.colorHex).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = currentStage.titleHindi,
                                            color = Color(currentStage.colorHex),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatrixHeaderCell(title: String, width: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier.width(width),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF334155)
        )
    }
}

@Composable
private fun MatrixStageCell(
    timeStr: String?,
    isCompleted: Boolean,
    width: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier.width(width),
        contentAlignment = Alignment.CenterStart
    ) {
        if (timeStr != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Done",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = timeStr,
                    fontSize = 10.sp,
                    color = Color(0xFF0F766E),
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else if (isCompleted) {
            Text(
                text = "✓ Done",
                fontSize = 10.sp,
                color = Color(0xFF10B981),
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = "—",
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1)
            )
        }
    }
}

private fun findStageTime(logs: List<TrackingLogEntity>, stage: TrackingStage): String? {
    val log = logs.firstOrNull { it.stage == stage.name } ?: return null
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(log.timestamp))
}

private fun getStageIcon(stage: TrackingStage): ImageVector {
    return when (stage) {
        TrackingStage.AT_HOME -> Icons.Default.Home
        TrackingStage.DEPARTED_HOME -> Icons.Default.DirectionsWalk
        TrackingStage.REACHED_SCHOOL_GATE -> Icons.Default.Security
        TrackingStage.ENTERED_CLASSROOM -> Icons.Default.School
        TrackingStage.LEFT_SCHOOL_GATE -> Icons.Default.DirectionsRun
        TrackingStage.REACHED_HOME -> Icons.Default.CheckCircle
    }
}
