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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.SchoolEntity
import com.example.ui.viewmodel.AppViewModel

@Composable
fun SuperAdminDashboard(
    viewModel: AppViewModel,
    onOpenSupabaseConfig: () -> Unit
) {
    val schools by viewModel.allSchools.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showAddSchoolDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSchoolDialog = true },
                containerColor = Color(0xFF6366F1),
                contentColor = Color.White,
                modifier = Modifier.testTag("add_school_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add School")
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
                color = Color(0xFF1E1B4B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6366F1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Super Admin Portal",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentUser?.fullName ?: "Central Control",
                                color = Color(0xFFC7D2FE),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = onOpenSupabaseConfig) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Supabase",
                                tint = Color(0xFF3ECF8E)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.testTag("logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SuperStatCard("Total Schools", schools.size.toString(), Color(0xFF6366F1), Modifier.weight(1f))
                SuperStatCard("Total Users", allUsers.size.toString(), Color(0xFF0F766E), Modifier.weight(1f))
                SuperStatCard("Admins", allUsers.count { it.role == "SCHOOL_ADMIN" }.toString(), Color(0xFF1E3A8A), Modifier.weight(1f))
            }

            Text(
                text = "REGISTERED INSTITUTES & SCHOOLS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(schools) { school ->
                    val adminUser = allUsers.find { it.schoolId == school.id && it.role == "SCHOOL_ADMIN" }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = Color(0xFF1E3A8A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = school.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEFF6FF)
                                ) {
                                    Text(
                                        text = school.schoolCode,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E3A8A),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "📍 ${school.address}", fontSize = 12.sp, color = Color(0xFF475569))
                            Text(text = "📞 ${school.contactNumber}", fontSize = 12.sp, color = Color(0xFF64748B))

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Admin: ${adminUser?.fullName ?: "Default Admin"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F766E)
                                )
                                Text(
                                    text = "Login: ${adminUser?.username ?: "admin"}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSchoolDialog) {
        AddSchoolDialog(
            onDismiss = { showAddSchoolDialog = false },
            onAdd = { name, code, address, phone ->
                viewModel.addSchool(name, code, address, phone)
                showAddSchoolDialog = false
            }
        )
    }
}

@Composable
private fun SuperStatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

@Composable
private fun AddSchoolDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, code: String, address: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Create New School",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Automatically generates a School Admin login account",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("School Name") },
                    placeholder = { Text("e.g. St. Xavier Global School") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("School Code") },
                    placeholder = { Text("e.g. SXGS-02") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Campus Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && code.isNotBlank()) {
                                onAdd(name, code, address, phone)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        Text("Save School")
                    }
                }
            }
        }
    }
}
