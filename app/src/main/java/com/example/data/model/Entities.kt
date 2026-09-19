package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "schools",
    indices = [Index(value = ["schoolCode"], unique = true)]
)
data class SchoolEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val schoolCode: String,
    val name: String,
    val address: String,
    val contactNumber: String,
    val establishedYear: String = "2015",
    val logoUrl: String = ""
)

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val password: String,
    val fullName: String,
    val role: String, // from Role.name
    val phone: String,
    val schoolId: Long = 1,
    val assignedClass: String = "", // e.g. "10th-A"
    val linkedStudentId: Long = 0   // for Parent
)

@Entity(
    tableName = "students",
    indices = [
        Index(value = ["studentUniqueId"], unique = true),
        Index(value = ["qrCodePayload"], unique = true)
    ]
)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentUniqueId: String,
    val schoolId: Long = 1,
    val name: String,
    val rollNo: String,
    val className: String, // e.g. "10th"
    val section: String = "A",   // e.g. "A"
    val fatherName: String = "",
    val motherName: String = "",
    val parentPhone: String = "",
    val parentUsername: String = "",
    val address: String = "",
    val bloodGroup: String = "B+",
    val photoUri: String = "",
    val photoBase64: String = "", // compressed <= 350kb
    val qrCodePayload: String,    // payload encoded in QR
    val emergencyContact: String = "",
    val currentStage: String = TrackingStage.AT_HOME.name,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
) {
    val fullClassSection: String
        get() = if (section.isNotBlank()) "$className-$section" else className
}

@Entity(tableName = "tracking_logs")
data class TrackingLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val schoolId: Long = 1,
    val className: String,
    val stage: String, // TrackingStage.name
    val timestamp: Long = System.currentTimeMillis(),
    val scannedByUserId: Long = 0,
    val scannedByName: String = "",
    val scannedByRole: String = "",
    val remarks: String = ""
)
