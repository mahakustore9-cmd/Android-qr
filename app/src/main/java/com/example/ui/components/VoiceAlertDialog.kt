package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.util.ActiveAlert

@Composable
fun VoiceAlertDialog(
    alert: ActiveAlert,
    onAcknowledge: () -> Unit
) {
    // Continuous pulse animation for alarm bell
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alarmPulse"
    )

    Dialog(
        onDismissRequest = { /* Non-dismissible without OK button as requested */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("voice_alert_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pulsing Alarm Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A), Color(0xFFF59E0B))
                            )
                        )
                        .border(3.dp, Color(0xFFD97706), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Alert Sounding",
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "🚨 LIVE NOTIFICATION ALERT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFD97706),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = alert.stageTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Spoken sentence box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Playing Audio",
                            tint = Color(0xFF1E3A8A),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "\"${alert.voiceMessage}\"",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E3A8A),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Super Tune & Hindi Voice continues playing until you tap OK.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Big OK / Acknowledge Button
                Button(
                    onClick = onAcknowledge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("acknowledge_alert_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OK, SAB THEEK HAI (DISMISS)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
