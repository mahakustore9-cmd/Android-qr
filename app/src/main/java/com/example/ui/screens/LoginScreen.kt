package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Role
import com.example.ui.viewmodel.AppViewModel

@Composable
fun LoginScreen(
    viewModel: AppViewModel,
    onOpenSupabaseConfig: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0F172A))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .verticalScroll(rememberScrollState())
                .testTag("login_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Shield / Logo
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(54.dp).clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Student Tracking System",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Smart Student Journey Safety & QR Scanning",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Unified Login Form
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = null
                    },
                    label = { Text("Username / User ID") },
                    placeholder = { Text("e.g. admin, guard1, parent1") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color(0xFF1E3A8A))
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Password") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF1E3A8A))
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input")
                )

                errorMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF2F2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (username.isBlank() || password.isBlank()) {
                            errorMessage = "Kripya username aur password enter karein."
                            return@Button
                        }
                        isLoading = true
                        viewModel.login(username, password) { success, msg ->
                            isLoading = false
                            if (!success) {
                                errorMessage = msg
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Text("LOGIN TO DASHBOARD", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Divider(color = Color(0xFFE2E8F0))

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Demo Preset Login Chips
                Text(
                    text = "Quick Demo Logins (1-Tap Switch):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DemoRoleChip("Super Admin", Color(0xFF6366F1), Modifier.weight(1f)) {
                        viewModel.quickLogin(Role.SUPER_ADMIN)
                    }
                    DemoRoleChip("School Admin", Color(0xFF1E3A8A), Modifier.weight(1f)) {
                        viewModel.quickLogin(Role.SCHOOL_ADMIN)
                    }
                    DemoRoleChip("Teacher", Color(0xFF0F766E), Modifier.weight(1f)) {
                        viewModel.quickLogin(Role.TEACHER)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DemoRoleChip("Gate Guard", Color(0xFFD97706), Modifier.weight(1f)) {
                        viewModel.quickLogin(Role.GATE_GUARD)
                    }
                    DemoRoleChip("Parent (Aarav)", Color(0xFF10B981), Modifier.weight(1f)) {
                        viewModel.quickLogin(Role.PARENT)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Supabase status bar
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenSupabaseConfig() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = null,
                                tint = if (viewModel.supabaseManager.isConfigured) Color(0xFF10B981) else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (viewModel.supabaseManager.isConfigured) "Supabase: Connected" else "Supabase: Offline Room DB Mode",
                                fontSize = 11.sp,
                                color = Color(0xFF334155),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = "Settings >",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoRoleChip(
    title: String,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = badgeColor.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f)),
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            color = badgeColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            maxLines = 1
        )
    }
}
