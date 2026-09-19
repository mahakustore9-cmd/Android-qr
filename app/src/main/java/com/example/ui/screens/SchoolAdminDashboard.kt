package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Role
import com.example.data.model.StudentEntity
import com.example.data.model.TrackingStage
import com.example.data.model.UserEntity
import com.example.ui.components.ClassJourneyMatrixTable
import com.example.ui.components.QrScannerDialog
import com.example.ui.components.StudentIdCard
import com.example.ui.viewmodel.AppViewModel
import com.example.util.ImageCompressor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SchoolAdminDashboard(
    viewModel: AppViewModel,
    onOpenSupabaseConfig: () -> Unit
) {
    val context = LocalContext.current
    val currentSchool by viewModel.currentSchool.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val filteredStudents by viewModel.filteredStudents.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val recentLogs by viewModel.recentLogs.collectAsState()
    val selectedClassFilter by viewModel.selectedClassFilter.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tracking Matrix", "Students", "User Logins", "Live Logs")

    var showScannerDialog by remember { mutableStateOf(false) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var studentToEdit by remember { mutableStateOf<StudentEntity?>(null) }
    var studentForIdCard by remember { mutableStateOf<StudentEntity?>(null) }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val classesList = listOf("All Classes", "10th-A", "8th-B", "5th-A")

    Scaffold(
        floatingActionButton = {
            if (selectedTabIndex == 1) {
                FloatingActionButton(
                    onClick = {
                        studentToEdit = null
                        showAddStudentDialog = true
                    },
                    containerColor = Color(0xFF1E3A8A),
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_student_fab")
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Student")
                }
            } else if (selectedTabIndex == 2) {
                FloatingActionButton(
                    onClick = { showAddUserDialog = true },
                    containerColor = Color(0xFF0F766E),
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_user_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Create Account")
                }
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
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = currentSchool?.name ?: "School Admin Portal",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "Admin Control • Session 2025-26",
                                color = Color(0xFF93C5FD),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = { showScannerDialog = true },
                            modifier = Modifier.testTag("open_scanner_header_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR",
                                tint = Color(0xFFFACC15)
                            )
                        }
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

            // Quick Metrics KPI Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminKpi("Enrolled", allStudents.size.toString(), Color(0xFF1E3A8A), Modifier.weight(1f))
                AdminKpi(
                    "At Gate",
                    allStudents.count { it.currentStage == TrackingStage.REACHED_SCHOOL_GATE.name }.toString(),
                    Color(0xFF3B82F6),
                    Modifier.weight(1f)
                )
                AdminKpi(
                    "In Class",
                    allStudents.count { it.currentStage == TrackingStage.ENTERED_CLASSROOM.name }.toString(),
                    Color(0xFF8B5CF6),
                    Modifier.weight(1f)
                )
                AdminKpi(
                    "Safely Home",
                    allStudents.count { it.currentStage == TrackingStage.REACHED_HOME.name || it.currentStage == TrackingStage.AT_HOME.name }.toString(),
                    Color(0xFF10B981),
                    Modifier.weight(1f)
                )
            }

            // Scrollable Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFF1E3A8A),
                edgePadding = 12.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            // Tab 0: Tracking Matrix
            if (selectedTabIndex == 0) {
                Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    // Class Filter row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter by Class:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(classesList) { cName ->
                                val isSelected = selectedClassFilter == cName
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) Color(0xFF1E3A8A) else Color(0xFFE2E8F0),
                                    modifier = Modifier.clickable { viewModel.selectClassFilter(cName) }
                                ) {
                                    Text(
                                        text = cName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF475569),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    ClassJourneyMatrixTable(
                        students = filteredStudents,
                        logs = recentLogs
                    )
                }
            }

            // Tab 1: Students Roster with ID Card & Edit/Delete
            if (selectedTabIndex == 1) {
                val displayedStudents = allStudents.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.rollNo.contains(searchQuery, ignoreCase = true) ||
                    it.fullClassSection.contains(searchQuery, ignoreCase = true)
                }

                Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search by name, roll no, or class") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("search_student_field")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(displayedStudents) { student ->
                            StudentAdminItemCard(
                                student = student,
                                onViewIdCard = { studentForIdCard = student },
                                onEdit = {
                                    studentToEdit = student
                                    showAddStudentDialog = true
                                },
                                onDelete = {
                                    viewModel.deleteStudent(student)
                                }
                            )
                        }
                    }
                }
            }

            // Tab 2: User Logins Management (Teachers, Guards, Parents)
            if (selectedTabIndex == 2) {
                val schoolUsers = allUsers.filter { it.schoolId == (currentSchool?.id ?: 1L) }
                Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Staff & Parent Accounts (${schoolUsers.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Button(
                            onClick = { showAddUserDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Account", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(schoolUsers) { user ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = user.fullName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "ID: ${user.username} • Pass: ${user.password}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1E3A8A)
                                        )
                                        if (user.assignedClass.isNotBlank()) {
                                            Text(
                                                text = "Assigned Class: ${user.assignedClass}",
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(Role.fromString(user.role).badgeColorHex).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = Role.fromString(user.role).displayName,
                                            color = Color(Role.fromString(user.role).badgeColorHex),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 3: Live Scan Logs Feed
            if (selectedTabIndex == 3) {
                Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Text(
                        text = "Real-time Scan Activities",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(recentLogs) { log ->
                            val stage = TrackingStage.fromName(log.stage)
                            val sdf = SimpleDateFormat("dd MMM, hh:mm:ss a", Locale.getDefault())
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(stage.colorHex).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QrCodeScanner,
                                            contentDescription = null,
                                            tint = Color(stage.colorHex),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${log.studentName} (${log.className})",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "${stage.titleHindi} • By ${log.scannedByName}",
                                            fontSize = 11.sp,
                                            color = Color(stage.colorHex),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = sdf.format(Date(log.timestamp)),
                                            fontSize = 10.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Scanner Dialog
    if (showScannerDialog) {
        QrScannerDialog(
            userRole = Role.SCHOOL_ADMIN,
            availableStudents = allStudents,
            onScanPayload = { payload, overrideStage ->
                viewModel.processScan(payload, overrideStage)
            },
            onDismiss = { showScannerDialog = false }
        )
    }

    // Add or Edit Student Dialog
    if (showAddStudentDialog) {
        AddEditStudentDialog(
            existingStudent = studentToEdit,
            onDismiss = { showAddStudentDialog = false },
            onSave = { name, roll, className, section, father, mother, phone, address, bg, emergency, photoBase64 ->
                viewModel.addOrUpdateStudent(
                    id = studentToEdit?.id ?: 0L,
                    name = name,
                    rollNo = roll,
                    className = className,
                    section = section,
                    fatherName = father,
                    motherName = mother,
                    parentPhone = phone,
                    address = address,
                    bloodGroup = bg,
                    emergencyContact = emergency,
                    photoBase64 = photoBase64
                )
                showAddStudentDialog = false
            }
        )
    }

    // View ID Card Modal Dialog
    studentForIdCard?.let { student ->
        Dialog(onDismissRequest = { studentForIdCard = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StudentIdCard(
                    student = student,
                    school = currentSchool
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        Toast.makeText(context, "ID Card ready for Print / Export!", Toast.LENGTH_SHORT).show()
                        studentForIdCard = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Done / Close")
                }
            }
        }
    }

    // Create User Account Dialog
    if (showAddUserDialog) {
        AddUserDialog(
            onDismiss = { showAddUserDialog = false },
            onSave = { username, password, fullName, role, phone, assignedClass ->
                viewModel.addUser(username, password, fullName, role, phone, assignedClass)
                showAddUserDialog = false
            }
        )
    }
}

@Composable
private fun AdminKpi(label: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = label, fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = count, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

@Composable
private fun StudentAdminItemCard(
    student: StudentEntity,
    onViewIdCard: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val stage = TrackingStage.fromName(student.currentStage)
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("student_item_${student.rollNo}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = student.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Class: ${student.fullClassSection} • Roll #${student.rollNo}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(stage.colorHex).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = stage.titleHindi,
                        color = Color(stage.colorHex),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Parent: ${student.fatherName.ifEmpty { "Guardian" }} • 📞 ${student.parentPhone}",
                fontSize = 11.sp,
                color = Color(0xFF475569)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View ID Card
                Button(
                    onClick = onViewIdCard,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("view_id_card_${student.rollNo}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null,
                        tint = Color(0xFF1E3A8A),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View ID Card & QR", color = Color(0xFF1E3A8A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2563EB))
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Student?") },
            text = { Text("Are you sure you want to remove ${student.name} from the system?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AddEditStudentDialog(
    existingStudent: StudentEntity?,
    onDismiss: () -> Unit,
    onSave: (
        name: String, roll: String, className: String, section: String,
        father: String, mother: String, phone: String, address: String,
        bg: String, emergency: String, photoBase64: String
    ) -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(existingStudent?.name ?: "") }
    var rollNo by remember { mutableStateOf(existingStudent?.rollNo ?: "") }
    var className by remember { mutableStateOf(existingStudent?.className ?: "10th") }
    var section by remember { mutableStateOf(existingStudent?.section ?: "A") }
    var fatherName by remember { mutableStateOf(existingStudent?.fatherName ?: "") }
    var motherName by remember { mutableStateOf(existingStudent?.motherName ?: "") }
    var phone by remember { mutableStateOf(existingStudent?.parentPhone ?: "") }
    var address by remember { mutableStateOf(existingStudent?.address ?: "") }
    var bloodGroup by remember { mutableStateOf(existingStudent?.bloodGroup ?: "B+") }
    var emergencyContact by remember { mutableStateOf(existingStudent?.emergencyContact ?: "") }
    var photoBase64 by remember { mutableStateOf(existingStudent?.photoBase64 ?: "") }
    var photoSizeInfo by remember { mutableStateOf("Photo size automatically compressed to <= 350 KB") }

    // Fast image picker with 350KB compressor
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val result = ImageCompressor.compressUri(context, uri)
            if (result != null) {
                photoBase64 = result.base64Data
                photoSizeInfo = "Compressed: ${String.format("%.1f", result.sizeInKb)} KB (Under 350 KB limit)"
                Toast.makeText(context, "Photo compressed (${String.format("%.1f", result.sizeInKb)} KB)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(4.dp).testTag("add_edit_student_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (existingStudent == null) "Add New Student" else "Edit Student",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Generates official ID card, QR code & Parent login automatically",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Photo upload section (<= 350KB)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF1F5F9))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Button(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Upload Photo (Max 350KB)", fontSize = 11.sp, color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = photoSizeInfo,
                            fontSize = 10.sp,
                            color = Color(0xFF059669),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rollNo,
                        onValueChange = { rollNo = it },
                        label = { Text("Roll No *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("student_roll_input")
                    )
                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        label = { Text("Class") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = section,
                        onValueChange = { section = it },
                        label = { Text("Sec") },
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = { bloodGroup = it },
                        label = { Text("Blood Group") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = emergencyContact,
                        onValueChange = { emergencyContact = it },
                        label = { Text("Emergency Contact") },
                        singleLine = true,
                        modifier = Modifier.weight(1.5f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fatherName,
                    onValueChange = { fatherName = it },
                    label = { Text("Father / Guardian Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Parent Mobile Number *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Home Residential Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && rollNo.isNotBlank()) {
                                onSave(name, rollNo, className, section, fatherName, motherName, phone, address, bloodGroup, emergencyContact, photoBase64)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("save_student_button")
                    ) {
                        Text("Save & Generate ID Card")
                    }
                }
            }
        }
    }
}

@Composable
private fun AddUserDialog(
    onDismiss: () -> Unit,
    onSave: (username: String, pass: String, fullName: String, role: Role, phone: String, assignedClass: String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(Role.TEACHER) }
    var phone by remember { mutableStateOf("") }
    var assignedClass by remember { mutableStateOf("10th-A") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Create Staff Account",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Role Selector Chips
                Text(text = "Select Role:", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(Role.TEACHER, Role.GATE_GUARD, Role.PARENT).forEach { r ->
                        val isSelected = role == r
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF1E3A8A) else Color(0xFFF1F5F9),
                            modifier = Modifier.clickable { role = r }
                        ) {
                            Text(
                                text = r.displayName,
                                color = if (isSelected) Color.White else Color(0xFF475569),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    placeholder = { Text("e.g. guard2, teacher_math") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (role == Role.TEACHER) {
                    OutlinedTextField(
                        value = assignedClass,
                        onValueChange = { assignedClass = it },
                        label = { Text("Assigned Class (e.g. 10th-A)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (username.isNotBlank() && password.isNotBlank()) {
                                onSave(username, password, fullName, role, phone, assignedClass)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                    ) {
                        Text("Create Account")
                    }
                }
            }
        }
    }
}
