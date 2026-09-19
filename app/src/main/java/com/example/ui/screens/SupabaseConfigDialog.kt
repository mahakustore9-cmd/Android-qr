package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.remote.SupabaseManager
import kotlinx.coroutines.launch

@Composable
fun SupabaseConfigDialog(
    supabaseManager: SupabaseManager,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var url by remember { mutableStateOf(supabaseManager.supabaseUrl) }
    var anonKey by remember { mutableStateOf(supabaseManager.anonKey) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var isTesting by remember { mutableStateOf(false) }
    var showSqlSchema by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("supabase_config_dialog"),
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = "Supabase",
                            tint = Color(0xFF3ECF8E), // Supabase Green
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Supabase Database Sync",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "App runs 100% offline with Room DB and automatically syncs logs with Supabase when configured.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Supabase Project URL") },
                    placeholder = { Text("https://your-project.supabase.co") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = anonKey,
                    onValueChange = { anonKey = it },
                    label = { Text("Supabase Anon Public API Key") },
                    placeholder = { Text("eyJhbGciOi...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons: Test Connection & View SQL Schema
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            supabaseManager.supabaseUrl = url
                            supabaseManager.anonKey = anonKey
                            isTesting = true
                            testResult = null
                            scope.launch {
                                val result = supabaseManager.testConnection()
                                isTesting = false
                                testResult = result.second
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Test Connection", fontSize = 12.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { showSqlSchema = !showSqlSchema },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (showSqlSchema) "Hide SQL" else "Auto SQL Script", fontSize = 12.sp)
                    }
                }

                testResult?.let { msg ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (msg.contains("Successful") || msg.contains("Connected")) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                    ) {
                        Text(
                            text = msg,
                            fontSize = 11.sp,
                            color = if (msg.contains("Successful") || msg.contains("Connected")) Color(0xFF047857) else Color(0xFFB91C1C),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                if (showSqlSchema) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "1-Click Supabase SQL Schema:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = SupabaseManager.AUTO_CREATE_TABLES_SQL,
                            color = Color(0xFF38BDF8),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Supabase Schema", SupabaseManager.AUTO_CREATE_TABLES_SQL)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "SQL Script Copied to Clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Full SQL to Clipboard")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            supabaseManager.supabaseUrl = url
                            supabaseManager.anonKey = anonKey
                            Toast.makeText(context, "Supabase Settings Saved!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Config")
                    }
                }
            }
        }
    }
}
