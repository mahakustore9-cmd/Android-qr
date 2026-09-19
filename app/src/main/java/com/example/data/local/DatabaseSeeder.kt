package com.example.data.local

import com.example.data.model.Role
import com.example.data.model.SchoolEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TrackingLogEntity
import com.example.data.model.TrackingStage
import com.example.data.model.UserEntity

object DatabaseSeeder {
    suspend fun seedInitialData(database: AppDatabase) {
        val schoolDao = database.schoolDao()
        val userDao = database.userDao()
        val studentDao = database.studentDao()
        val trackingDao = database.trackingLogDao()

        // 1. School
        val schoolId = schoolDao.insertSchool(
            SchoolEntity(
                schoolCode = "DPGA-01",
                name = "Delhi Public Global Academy",
                address = "Sector 62, Institutional Area, Noida",
                contactNumber = "+91 98765 43210",
                establishedYear = "2012"
            )
        )

        // 2. Users
        userDao.insertUser(
            UserEntity(
                username = "superadmin",
                password = "admin123",
                fullName = "Dr. Vikram Seth (Super Admin)",
                role = Role.SUPER_ADMIN.name,
                phone = "+91 99999 00001",
                schoolId = 0
            )
        )

        userDao.insertUser(
            UserEntity(
                username = "admin",
                password = "admin123",
                fullName = "Dr. Rajesh Sharma (Principal)",
                role = Role.SCHOOL_ADMIN.name,
                phone = "+91 98765 11001",
                schoolId = schoolId
            )
        )

        userDao.insertUser(
            UserEntity(
                username = "teacher10",
                password = "teacher123",
                fullName = "Mrs. Sunita Verma (Class 10th-A)",
                role = Role.TEACHER.name,
                phone = "+91 98765 22001",
                schoolId = schoolId,
                assignedClass = "10th-A"
            )
        )

        userDao.insertUser(
            UserEntity(
                username = "teacher8",
                password = "teacher123",
                fullName = "Mr. Amit Kapoor (Class 8th-B)",
                role = Role.TEACHER.name,
                phone = "+91 98765 22002",
                schoolId = schoolId,
                assignedClass = "8th-B"
            )
        )

        userDao.insertUser(
            UserEntity(
                username = "guard1",
                password = "guard123",
                fullName = "Ramesh Kumar (Main Gate Security)",
                role = Role.GATE_GUARD.name,
                phone = "+91 98765 33001",
                schoolId = schoolId
            )
        )

        // 3. Students
        val student1Id = studentDao.insertStudent(
            StudentEntity(
                studentUniqueId = "DPGA-2025-101",
                schoolId = schoolId,
                name = "Aarav Sharma",
                rollNo = "101",
                className = "10th",
                section = "A",
                fatherName = "Vikas Sharma",
                motherName = "Pooja Sharma",
                parentPhone = "+91 98765 44001",
                parentUsername = "parent1",
                address = "Flat 402, Lotus Boulevard, Sector 100",
                bloodGroup = "B+",
                qrCodePayload = "STU-DPGA-2025-101-AARAV",
                emergencyContact = "+91 98765 44001",
                currentStage = TrackingStage.ENTERED_CLASSROOM.name,
                lastUpdatedTimestamp = System.currentTimeMillis() - (15 * 60 * 1000)
            )
        )

        val student2Id = studentDao.insertStudent(
            StudentEntity(
                studentUniqueId = "DPGA-2025-102",
                schoolId = schoolId,
                name = "Ananya Patel",
                rollNo = "102",
                className = "10th",
                section = "A",
                fatherName = "Manoj Patel",
                motherName = "Neeta Patel",
                parentPhone = "+91 98765 44002",
                parentUsername = "parent2",
                address = "House 88, ATS Greens Village, Sector 93A",
                bloodGroup = "O+",
                qrCodePayload = "STU-DPGA-2025-102-ANANYA",
                emergencyContact = "+91 98765 44002",
                currentStage = TrackingStage.REACHED_SCHOOL_GATE.name,
                lastUpdatedTimestamp = System.currentTimeMillis() - (35 * 60 * 1000)
            )
        )

        val student3Id = studentDao.insertStudent(
            StudentEntity(
                studentUniqueId = "DPGA-2025-201",
                schoolId = schoolId,
                name = "Rohan Verma",
                rollNo = "201",
                className = "8th",
                section = "B",
                fatherName = "Rakesh Verma",
                motherName = "Kavita Verma",
                parentPhone = "+91 98765 44003",
                parentUsername = "parent3",
                address = "Tower C-501, Supertech Capetown",
                bloodGroup = "A+",
                qrCodePayload = "STU-DPGA-2025-201-ROHAN",
                emergencyContact = "+91 98765 44003",
                currentStage = TrackingStage.DEPARTED_HOME.name,
                lastUpdatedTimestamp = System.currentTimeMillis() - (50 * 60 * 1000)
            )
        )

        val student4Id = studentDao.insertStudent(
            StudentEntity(
                studentUniqueId = "DPGA-2025-202",
                schoolId = schoolId,
                name = "Priya Singh",
                rollNo = "202",
                className = "8th",
                section = "B",
                fatherName = "Sunil Singh",
                motherName = "Rekha Singh",
                parentPhone = "+91 98765 44004",
                parentUsername = "parent4",
                address = "Villa 12, Jaypee Greens, Greater Noida",
                bloodGroup = "AB+",
                qrCodePayload = "STU-DPGA-2025-202-PRIYA",
                emergencyContact = "+91 98765 44004",
                currentStage = TrackingStage.ENTERED_CLASSROOM.name,
                lastUpdatedTimestamp = System.currentTimeMillis() - (20 * 60 * 1000)
            )
        )

        val student5Id = studentDao.insertStudent(
            StudentEntity(
                studentUniqueId = "DPGA-2025-301",
                schoolId = schoolId,
                name = "Kabir Khan",
                rollNo = "301",
                className = "5th",
                section = "A",
                fatherName = "Imran Khan",
                motherName = "Farida Khan",
                parentPhone = "+91 98765 44005",
                parentUsername = "parent5",
                address = "Sector 50, Block B, House 24",
                bloodGroup = "O-",
                qrCodePayload = "STU-DPGA-2025-301-KABIR",
                emergencyContact = "+91 98765 44005",
                currentStage = TrackingStage.AT_HOME.name,
                lastUpdatedTimestamp = System.currentTimeMillis() - (120 * 60 * 1000)
            )
        )

        // Parents accounts linked to students
        userDao.insertUser(
            UserEntity(
                username = "parent1",
                password = "parent123",
                fullName = "Vikas Sharma (Aarav's Father)",
                role = Role.PARENT.name,
                phone = "+91 98765 44001",
                schoolId = schoolId,
                linkedStudentId = student1Id
            )
        )

        userDao.insertUser(
            UserEntity(
                username = "parent2",
                password = "parent123",
                fullName = "Manoj Patel (Ananya's Father)",
                role = Role.PARENT.name,
                phone = "+91 98765 44002",
                schoolId = schoolId,
                linkedStudentId = student2Id
            )
        )

        // 4. Initial Tracking Logs
        val now = System.currentTimeMillis()
        trackingDao.insertLog(
            TrackingLogEntity(
                studentId = student1Id,
                studentName = "Aarav Sharma",
                schoolId = schoolId,
                className = "10th-A",
                stage = TrackingStage.DEPARTED_HOME.name,
                timestamp = now - (60 * 60 * 1000),
                scannedByName = "Vikas Sharma",
                scannedByRole = Role.PARENT.name,
                remarks = "Ghar se school bus ke liye nikla"
            )
        )
        trackingDao.insertLog(
            TrackingLogEntity(
                studentId = student1Id,
                studentName = "Aarav Sharma",
                schoolId = schoolId,
                className = "10th-A",
                stage = TrackingStage.REACHED_SCHOOL_GATE.name,
                timestamp = now - (35 * 60 * 1000),
                scannedByName = "Ramesh Kumar (Guard)",
                scannedByRole = Role.GATE_GUARD.name,
                remarks = "School Main Gate scanned IN"
            )
        )
        trackingDao.insertLog(
            TrackingLogEntity(
                studentId = student1Id,
                studentName = "Aarav Sharma",
                schoolId = schoolId,
                className = "10th-A",
                stage = TrackingStage.ENTERED_CLASSROOM.name,
                timestamp = now - (15 * 60 * 1000),
                scannedByName = "Mrs. Sunita Verma",
                scannedByRole = Role.TEACHER.name,
                remarks = "Roll Call marked inside Class 10th-A"
            )
        )

        trackingDao.insertLog(
            TrackingLogEntity(
                studentId = student2Id,
                studentName = "Ananya Patel",
                schoolId = schoolId,
                className = "10th-A",
                stage = TrackingStage.DEPARTED_HOME.name,
                timestamp = now - (75 * 60 * 1000),
                scannedByName = "Manoj Patel",
                scannedByRole = Role.PARENT.name
            )
        )
        trackingDao.insertLog(
            TrackingLogEntity(
                studentId = student2Id,
                studentName = "Ananya Patel",
                schoolId = schoolId,
                className = "10th-A",
                stage = TrackingStage.REACHED_SCHOOL_GATE.name,
                timestamp = now - (30 * 60 * 1000),
                scannedByName = "Ramesh Kumar (Guard)",
                scannedByRole = Role.GATE_GUARD.name,
                remarks = "School Main Gate scanned IN"
            )
        )
    }
}
