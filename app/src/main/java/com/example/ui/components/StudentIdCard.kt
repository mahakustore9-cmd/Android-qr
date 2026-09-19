package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchoolEntity
import com.example.data.model.StudentEntity
import com.example.util.ImageCompressor
import com.example.util.QrCodeGenerator

@Composable
fun StudentIdCard(
    student: StudentEntity,
    school: SchoolEntity?,
    modifier: Modifier = Modifier
) {
    val schoolName = school?.name ?: "Delhi Public Global Academy"
    val schoolCode = school?.schoolCode ?: "DPGA-01"

    // Generate QR bitmap memoized
    val qrBitmap = remember(student.qrCodePayload) {
        QrCodeGenerator.generateQrBitmap(student.qrCodePayload, 220)
    }

    // Avatar bitmap memoized
    val avatarBitmap = remember(student.name, student.rollNo) {
        ImageCompressor.generateAvatarBitmap(student.name, student.rollNo)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF1E3A8A), Color(0xFF2563EB))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "School Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = schoolName,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "AFFILIATED • SESSION 2025-26 • $schoolCode",
                            color = Color(0xFF93C5FD),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF59E0B)
                    ) {
                        Text(
                            text = "STUDENT",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Body
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Photo & Quick Badges
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(100.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, Color(0xFF2563EB), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = avatarBitmap.asImageBitmap(),
                            contentDescription = "Student Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFEFF6FF)
                    ) {
                        Text(
                            text = "BG: ${student.bloodGroup}",
                            color = Color(0xFF1E3A8A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Student Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "ID: ${student.studentUniqueId}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    IdCardField("Class & Sec", student.fullClassSection)
                    IdCardField("Roll No", student.rollNo)
                    if (student.fatherName.isNotBlank()) {
                        IdCardField("Parent", student.fatherName)
                    }
                    IdCardField("Emergency", student.emergencyContact.ifEmpty { student.parentPhone })
                }
            }

            Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // Bottom Bar: QR Code & Security Stamp
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // QR Code
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Student QR Code",
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "SCAN FOR TRACKING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = "Auto-Synced Live Journey",
                            fontSize = 9.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Official Seal
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Seal",
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "OFFICIAL ID",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                    }
                    Text(
                        text = "Principal Signature",
                        fontSize = 9.sp,
                        color = Color(0xFF94A3B8),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
private fun IdCardField(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(75.dp)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color(0xFF1E293B),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}
