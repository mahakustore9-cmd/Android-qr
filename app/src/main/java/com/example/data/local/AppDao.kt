package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SchoolEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TrackingLogEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    @Query("SELECT * FROM schools WHERE id = :id LIMIT 1")
    fun getSchoolById(id: Long): Flow<SchoolEntity?>

    @Query("SELECT * FROM schools ORDER BY name ASC")
    fun getAllSchools(): Flow<List<SchoolEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchool(school: SchoolEntity): Long

    @Update
    suspend fun updateSchool(school: SchoolEntity)

    @Delete
    suspend fun deleteSchool(school: SchoolEntity)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) AND password = :password LIMIT 1")
    suspend fun authenticate(username: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE schoolId = :schoolId AND role = :role ORDER BY fullName ASC")
    fun getUsersBySchoolAndRole(schoolId: Long, role: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY fullName ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteUserByUsername(username: String)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE schoolId = :schoolId ORDER BY name ASC")
    fun getAllStudents(schoolId: Long): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE schoolId = :schoolId AND className = :className AND section = :section ORDER BY rollNo ASC")
    fun getStudentsByClass(schoolId: Long, className: String, section: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE schoolId = :schoolId AND className = :className ORDER BY rollNo ASC")
    fun getStudentsByClassNameOnly(schoolId: Long, className: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    fun getStudentById(id: Long): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentByIdDirect(id: Long): StudentEntity?

    @Query("SELECT * FROM students WHERE qrCodePayload = :payload LIMIT 1")
    suspend fun getStudentByQrPayload(payload: String): StudentEntity?

    @Query("SELECT * FROM students WHERE LOWER(studentUniqueId) = LOWER(:uniqueId) LIMIT 1")
    suspend fun getStudentByUniqueId(uniqueId: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("UPDATE students SET currentStage = :stage, lastUpdatedTimestamp = :timestamp WHERE id = :studentId")
    suspend fun updateStage(studentId: Long, stage: String, timestamp: Long)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    @Query("DELETE FROM students WHERE id = :studentId")
    suspend fun deleteStudentById(studentId: Long)
}

@Dao
interface TrackingLogDao {
    @Query("SELECT * FROM tracking_logs WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getLogsForStudent(studentId: Long): Flow<List<TrackingLogEntity>>

    @Query("SELECT * FROM tracking_logs WHERE schoolId = :schoolId ORDER BY timestamp DESC")
    fun getLogsForSchool(schoolId: Long): Flow<List<TrackingLogEntity>>

    @Query("SELECT * FROM tracking_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 100): Flow<List<TrackingLogEntity>>

    @Query("SELECT * FROM tracking_logs WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getLogsByDateRange(startTime: Long, endTime: Long): Flow<List<TrackingLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: TrackingLogEntity): Long
}
