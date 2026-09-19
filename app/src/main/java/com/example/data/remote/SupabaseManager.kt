package com.example.data.remote

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.model.SchoolEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TrackingLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("supabase_prefs", Context.MODE_PRIVATE)

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    var supabaseUrl: String
        get() = prefs.getString("supabase_url", "https://xyzcompany.supabase.co") ?: ""
        set(value) = prefs.edit().putString("supabase_url", value.trim().removeSuffix("/")).apply()

    var anonKey: String
        get() = prefs.getString("supabase_anon_key", "") ?: ""
        set(value) = prefs.edit().putString("supabase_anon_key", value.trim()).apply()

    val isConfigured: Boolean
        get() = supabaseUrl.isNotBlank() && anonKey.isNotBlank() && !supabaseUrl.contains("xyzcompany")

    suspend fun testConnection(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        if (!isConfigured) {
            return@withContext Pair(false, "Supabase URL ya Anon Key configure nahi hai.")
        }
        try {
            val request = Request.Builder()
                .url("$supabaseUrl/rest/v1/students?select=count")
                .header("apikey", anonKey)
                .header("Authorization", "Bearer $anonKey")
                .header("Range", "0-0")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Pair(true, "Supabase Connection Successful! (HTTP ${response.code})")
                } else if (response.code == 404 || response.code == 400) {
                    Pair(true, "Connected to Supabase! (Note: Tables will be created using the SQL script)")
                } else {
                    Pair(false, "Supabase Error: ${response.code} ${response.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseManager", "Connection error", e)
            Pair(false, "Connection Failed: ${e.localizedMessage}")
        }
    }

    suspend fun syncTrackingLog(log: TrackingLogEntity): Boolean = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext false
        try {
            val json = JSONObject().apply {
                put("student_id", log.studentId)
                put("student_name", log.studentName)
                put("class_name", log.className)
                put("stage", log.stage)
                put("timestamp", log.timestamp)
                put("scanned_by_name", log.scannedByName)
                put("scanned_by_role", log.scannedByRole)
                put("remarks", log.remarks)
            }

            val body = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$supabaseUrl/rest/v1/tracking_logs")
                .header("apikey", anonKey)
                .header("Authorization", "Bearer $anonKey")
                .header("Prefer", "return=minimal")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            Log.e("SupabaseManager", "Sync log error", e)
            false
        }
    }

    companion object {
        val AUTO_CREATE_TABLES_SQL = """
            -- ========================================================
            -- STUDENT TRACKING SYSTEM - SUPABASE AUTO SCHEMA SCRIPT
            -- Run this in your Supabase SQL Editor (1-Click Setup)
            -- ========================================================

            -- 1. Schools Table
            CREATE TABLE IF NOT EXISTS public.schools (
                id BIGSERIAL PRIMARY KEY,
                school_code TEXT UNIQUE NOT NULL,
                name TEXT NOT NULL,
                address TEXT,
                contact_number TEXT,
                established_year TEXT DEFAULT '2015',
                logo_url TEXT,
                created_at TIMESTAMPTZ DEFAULT NOW()
            );

            -- 2. App Users Table (Multi-Role Login)
            CREATE TABLE IF NOT EXISTS public.users (
                id BIGSERIAL PRIMARY KEY,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                full_name TEXT NOT NULL,
                role TEXT NOT NULL, -- SUPER_ADMIN, SCHOOL_ADMIN, TEACHER, GATE_GUARD, PARENT
                phone TEXT,
                school_id BIGINT REFERENCES public.schools(id) ON DELETE CASCADE,
                assigned_class TEXT, -- e.g. '10th-A'
                linked_student_id BIGINT,
                created_at TIMESTAMPTZ DEFAULT NOW()
            );

            -- 3. Students Table
            CREATE TABLE IF NOT EXISTS public.students (
                id BIGSERIAL PRIMARY KEY,
                student_unique_id TEXT UNIQUE NOT NULL,
                school_id BIGINT REFERENCES public.schools(id) ON DELETE CASCADE,
                name TEXT NOT NULL,
                roll_no TEXT NOT NULL,
                class_name TEXT NOT NULL,
                section TEXT DEFAULT 'A',
                father_name TEXT,
                mother_name TEXT,
                parent_phone TEXT,
                parent_username TEXT,
                address TEXT,
                blood_group TEXT DEFAULT 'B+',
                photo_url TEXT,
                qr_code_payload TEXT UNIQUE NOT NULL,
                emergency_contact TEXT,
                current_stage TEXT DEFAULT 'AT_HOME',
                last_updated_timestamp BIGINT DEFAULT EXTRACT(EPOCH FROM NOW()) * 1000,
                created_at TIMESTAMPTZ DEFAULT NOW()
            );

            -- 4. Real-time Tracking Journey Logs Table
            CREATE TABLE IF NOT EXISTS public.tracking_logs (
                id BIGSERIAL PRIMARY KEY,
                student_id BIGINT REFERENCES public.students(id) ON DELETE CASCADE,
                student_name TEXT NOT NULL,
                school_id BIGINT,
                class_name TEXT NOT NULL,
                stage TEXT NOT NULL, -- DEPARTED_HOME, REACHED_SCHOOL_GATE, ENTERED_CLASSROOM, LEFT_SCHOOL_GATE, REACHED_HOME
                timestamp BIGINT NOT NULL,
                scanned_by_user_id BIGINT,
                scanned_by_name TEXT,
                scanned_by_role TEXT,
                remarks TEXT,
                created_at TIMESTAMPTZ DEFAULT NOW()
            );

            -- Enable RLS and Permissive Policies for quick start
            ALTER TABLE public.schools ENABLE ROW LEVEL SECURITY;
            ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
            ALTER TABLE public.students ENABLE ROW LEVEL SECURITY;
            ALTER TABLE public.tracking_logs ENABLE ROW LEVEL SECURITY;

            DO $$ BEGIN
                CREATE POLICY "Allow public read-write" ON public.schools FOR ALL USING (true) WITH CHECK (true);
                CREATE POLICY "Allow public read-write" ON public.users FOR ALL USING (true) WITH CHECK (true);
                CREATE POLICY "Allow public read-write" ON public.students FOR ALL USING (true) WITH CHECK (true);
                CREATE POLICY "Allow public read-write" ON public.tracking_logs FOR ALL USING (true) WITH CHECK (true);
            EXCEPTION WHEN OTHERS THEN NULL;
            END $$;

            -- Storage bucket for photos (Limit <= 350KB)
            INSERT INTO storage.buckets (id, name, public, file_size_limit)
            VALUES ('student_photos', 'student_photos', true, 358400)
            ON CONFLICT (id) DO NOTHING;
        """.trimIndent()
    }
}
