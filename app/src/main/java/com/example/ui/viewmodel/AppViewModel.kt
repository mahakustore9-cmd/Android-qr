package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Role
import com.example.data.model.SchoolEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TrackingLogEntity
import com.example.data.model.TrackingStage
import com.example.data.model.UserEntity
import com.example.data.remote.SupabaseManager
import com.example.util.ActiveAlert
import com.example.util.ImageCompressor
import com.example.util.VoiceAlertManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class ScanResult {
    data class Success(val student: StudentEntity, val stage: TrackingStage, val message: String) : ScanResult()
    data class Error(val errorMsg: String) : ScanResult()
}

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val schoolDao = database.schoolDao()
    private val userDao = database.userDao()
    private val studentDao = database.studentDao()
    private val trackingLogDao = database.trackingLogDao()

    val supabaseManager = SupabaseManager(application)
    val voiceAlertManager = VoiceAlertManager(application)

    // Current Session
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val currentSchool: StateFlow<SchoolEntity?> = _currentUser.flatMapLatest { user ->
        val sId = user?.schoolId ?: 1L
        if (sId > 0) schoolDao.getSchoolById(sId) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All Schools (for Super Admin)
    val allSchools: StateFlow<List<SchoolEntity>> = schoolDao.getAllSchools()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Users
    val allUsers: StateFlow<List<UserEntity>> = userDao.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Class filter
    private val _selectedClassFilter = MutableStateFlow("All Classes")
    val selectedClassFilter: StateFlow<String> = _selectedClassFilter.asStateFlow()

    // Students Flow
    val allStudents: StateFlow<List<StudentEntity>> = _currentUser.flatMapLatest { user ->
        val sId = user?.schoolId ?: 1L
        studentDao.getAllStudents(sId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Students for Matrix or Teacher
    val filteredStudents: StateFlow<List<StudentEntity>> = combine(allStudents, _selectedClassFilter) { students, filter ->
        if (filter == "All Classes") {
            students
        } else {
            students.filter { it.fullClassSection.equals(filter, ignoreCase = true) || it.className.equals(filter, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recent Tracking Logs
    val recentLogs: StateFlow<List<TrackingLogEntity>> = trackingLogDao.getRecentLogs(100)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active voice alert loop for Parent
    val activeParentAlert: StateFlow<ActiveAlert?> = voiceAlertManager.activeAlert

    // UI feedback events
    private val _scanEvent = MutableSharedFlow<ScanResult>()
    val scanEvent: SharedFlow<ScanResult> = _scanEvent.asSharedFlow()

    private val _uiToast = MutableSharedFlow<String>()
    val uiToast: SharedFlow<String> = _uiToast.asSharedFlow()

    fun selectClassFilter(className: String) {
        _selectedClassFilter.value = className
    }

    // --- Authentication ---
    fun login(username: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.authenticate(username.trim(), pass.trim())
            withContext(Dispatchers.Main) {
                if (user != null) {
                    _currentUser.value = user
                    onResult(true, "Swagatam, ${user.fullName}!")
                } else {
                    onResult(false, "Galat username ya password! Kripya dobara janch karein.")
                }
            }
        }
    }

    fun quickLogin(role: Role) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = when (role) {
                Role.SUPER_ADMIN -> userDao.authenticate("superadmin", "admin123")
                Role.SCHOOL_ADMIN -> userDao.authenticate("admin", "admin123")
                Role.TEACHER -> userDao.authenticate("teacher10", "teacher123")
                Role.GATE_GUARD -> userDao.authenticate("guard1", "guard123")
                Role.PARENT -> userDao.authenticate("parent1", "parent123")
            }
            withContext(Dispatchers.Main) {
                if (user != null) {
                    _currentUser.value = user
                }
            }
        }
    }

    fun logout() {
        voiceAlertManager.stopPersistentAlert()
        _currentUser.value = null
    }

    // --- Student Management (Admin) ---
    fun addOrUpdateStudent(
        id: Long = 0,
        name: String,
        rollNo: String,
        className: String,
        section: String,
        fatherName: String,
        motherName: String,
        parentPhone: String,
        address: String,
        bloodGroup: String,
        emergencyContact: String,
        photoBase64: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val schoolId = _currentUser.value?.schoolId ?: 1L
            val cleanRoll = rollNo.trim().ifEmpty { "100" }
            val cleanClass = className.trim().ifEmpty { "10th" }
            val cleanSection = section.trim().uppercase().ifEmpty { "A" }
            val uniqueId = "DPGA-2025-$cleanRoll-${System.currentTimeMillis() % 1000}"
            val qrPayload = "STU-DPGA-$cleanRoll-${name.uppercase().replace(" ", "")}"

            val parentUsername = "parent_$cleanRoll"

            val student = StudentEntity(
                id = id,
                studentUniqueId = uniqueId,
                schoolId = schoolId,
                name = name.trim(),
                rollNo = cleanRoll,
                className = cleanClass,
                section = cleanSection,
                fatherName = fatherName.trim(),
                motherName = motherName.trim(),
                parentPhone = parentPhone.trim(),
                parentUsername = parentUsername,
                address = address.trim(),
                bloodGroup = bloodGroup.trim().ifEmpty { "B+" },
                photoBase64 = photoBase64,
                qrCodePayload = qrPayload,
                emergencyContact = emergencyContact.trim().ifEmpty { parentPhone.trim() }
            )

            val studentId = if (id == 0L) {
                val newId = studentDao.insertStudent(student)
                // Automatically create parent account credentials
                userDao.insertUser(
                    UserEntity(
                        username = parentUsername,
                        password = "parent123",
                        fullName = if (fatherName.isNotBlank()) fatherName else "${name}'s Parent",
                        role = Role.PARENT.name,
                        phone = parentPhone,
                        schoolId = schoolId,
                        linkedStudentId = newId
                    )
                )
                newId
            } else {
                studentDao.updateStudent(student)
                id
            }

            withContext(Dispatchers.Main) {
                _uiToast.emit(if (id == 0L) "Student '$name' successfully added!" else "Student '$name' updated!")
            }
        }
    }

    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            studentDao.deleteStudent(student)
            withContext(Dispatchers.Main) {
                _uiToast.emit("Student '${student.name}' removed.")
            }
        }
    }

    // --- User Management (Admin) ---
    fun addUser(
        username: String,
        pass: String,
        fullName: String,
        role: Role,
        phone: String,
        assignedClass: String = ""
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val schoolId = _currentUser.value?.schoolId ?: 1L
            val user = UserEntity(
                username = username.trim().lowercase(),
                password = pass.trim(),
                fullName = fullName.trim(),
                role = role.name,
                phone = phone.trim(),
                schoolId = schoolId,
                assignedClass = assignedClass.trim()
            )
            userDao.insertUser(user)
            withContext(Dispatchers.Main) {
                _uiToast.emit("${role.displayName} account created for $fullName")
            }
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.deleteUser(user)
            withContext(Dispatchers.Main) {
                _uiToast.emit("User '${user.fullName}' deleted.")
            }
        }
    }

    // --- School Management (Super Admin) ---
    fun addSchool(name: String, code: String, address: String, contactNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val school = SchoolEntity(
                schoolCode = code.trim().uppercase(),
                name = name.trim(),
                address = address.trim(),
                contactNumber = contactNumber.trim()
            )
            val newSchoolId = schoolDao.insertSchool(school)
            // Create default School Admin
            userDao.insertUser(
                UserEntity(
                    username = "admin_${code.lowercase().replace("-", "")}",
                    password = "admin123",
                    fullName = "$name Admin",
                    role = Role.SCHOOL_ADMIN.name,
                    phone = contactNumber,
                    schoolId = newSchoolId
                )
            )
            withContext(Dispatchers.Main) {
                _uiToast.emit("School '$name' registered with Admin account!")
            }
        }
    }

    // --- QR Scanning & Stage Progression ---
    fun processScan(qrPayload: String, targetStageOverride: TrackingStage? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val student = studentDao.getStudentByQrPayload(qrPayload.trim())
                ?: studentDao.getStudentByUniqueId(qrPayload.trim())

            if (student == null) {
                val errorResult = ScanResult.Error("QR Code anjaan hai! Student nahi mila ($qrPayload)")
                withContext(Dispatchers.Main) {
                    _scanEvent.emit(errorResult)
                    _uiToast.emit("QR Code not recognized")
                }
                return@launch
            }

            val user = _currentUser.value
            val userRole = if (user != null) Role.fromString(user.role) else Role.GATE_GUARD

            // Determine target stage based on scanner role or explicit override
            val targetStage: TrackingStage = targetStageOverride ?: when (userRole) {
                Role.PARENT -> {
                    val current = TrackingStage.fromName(student.currentStage)
                    if (current == TrackingStage.AT_HOME || current == TrackingStage.REACHED_HOME) {
                        TrackingStage.DEPARTED_HOME
                    } else {
                        TrackingStage.REACHED_HOME
                    }
                }
                Role.GATE_GUARD -> {
                    val current = TrackingStage.fromName(student.currentStage)
                    if (current == TrackingStage.DEPARTED_HOME || current == TrackingStage.AT_HOME) {
                        TrackingStage.REACHED_SCHOOL_GATE
                    } else {
                        TrackingStage.LEFT_SCHOOL_GATE
                    }
                }
                Role.TEACHER -> TrackingStage.ENTERED_CLASSROOM
                Role.SCHOOL_ADMIN, Role.SUPER_ADMIN -> {
                    // Advance to next logical stage
                    val current = TrackingStage.fromName(student.currentStage)
                    when (current) {
                        TrackingStage.AT_HOME -> TrackingStage.DEPARTED_HOME
                        TrackingStage.DEPARTED_HOME -> TrackingStage.REACHED_SCHOOL_GATE
                        TrackingStage.REACHED_SCHOOL_GATE -> TrackingStage.ENTERED_CLASSROOM
                        TrackingStage.ENTERED_CLASSROOM -> TrackingStage.LEFT_SCHOOL_GATE
                        TrackingStage.LEFT_SCHOOL_GATE -> TrackingStage.REACHED_HOME
                        TrackingStage.REACHED_HOME -> TrackingStage.DEPARTED_HOME
                    }
                }
            }

            val now = System.currentTimeMillis()
            // Update Student stage in Room
            studentDao.updateStage(student.id, targetStage.name, now)

            // Log entry
            val log = TrackingLogEntity(
                studentId = student.id,
                studentName = student.name,
                schoolId = student.schoolId,
                className = student.fullClassSection,
                stage = targetStage.name,
                timestamp = now,
                scannedByUserId = user?.id ?: 0L,
                scannedByName = user?.fullName ?: "Staff",
                scannedByRole = userRole.name,
                remarks = "${targetStage.titleHindi} (${targetStage.titleEnglish})"
            )
            trackingLogDao.insertLog(log)

            // Attempt sync to Supabase in background
            supabaseManager.syncTrackingLog(log)

            // Voice synthesis with tune
            val voiceMsg = targetStage.formatVoiceMessage(student.name)
            withContext(Dispatchers.Main) {
                // If scanned at Gate (Arrival or Exit), trigger persistent Parent alert loop!
                if (targetStage == TrackingStage.REACHED_SCHOOL_GATE || targetStage == TrackingStage.LEFT_SCHOOL_GATE) {
                    voiceAlertManager.startPersistentAlert(
                        studentName = student.name,
                        stageTitle = targetStage.titleHindi,
                        message = voiceMsg
                    )
                } else {
                    voiceAlertManager.speakOnce(voiceMsg)
                }

                val successResult = ScanResult.Success(student, targetStage, voiceMsg)
                _scanEvent.emit(successResult)
                _uiToast.emit("${student.name}: ${targetStage.titleHindi}")
            }
        }
    }

    /**
     * Parent acknowledges and dismisses continuous ringing voice alert
     */
    fun dismissVoiceAlert() {
        voiceAlertManager.stopPersistentAlert()
    }

    override fun onCleared() {
        super.onCleared()
        voiceAlertManager.release()
    }
}
