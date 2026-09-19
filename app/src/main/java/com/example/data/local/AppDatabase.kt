package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.SchoolEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TrackingLogEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SchoolEntity::class,
        UserEntity::class,
        StudentEntity::class,
        TrackingLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun schoolDao(): SchoolDao
    abstract fun userDao(): UserDao
    abstract fun studentDao(): StudentDao
    abstract fun trackingLogDao(): TrackingLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_tracking_system.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        DatabaseSeeder.seedInitialData(database)
                    }
                }
            }
        }
    }
}
