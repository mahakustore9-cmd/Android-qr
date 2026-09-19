package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.data.model.Role
import com.example.ui.screens.GuardDashboard
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ParentDashboard
import com.example.ui.screens.SchoolAdminDashboard
import com.example.ui.screens.SuperAdminDashboard
import com.example.ui.screens.SupabaseConfigDialog
import com.example.ui.screens.TeacherDashboard
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainApp(viewModel: AppViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    var showSupabaseConfig by remember { mutableStateOf(false) }

    // Listen to UI toast events
    LaunchedEffect(Unit) {
        viewModel.uiToast.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    if (currentUser == null) {
        LoginScreen(
            viewModel = viewModel,
            onOpenSupabaseConfig = { showSupabaseConfig = true }
        )
    } else {
        val user = currentUser ?: return
        val role = Role.fromString(user.role)
        when (role) {
            Role.SUPER_ADMIN -> {
                SuperAdminDashboard(
                    viewModel = viewModel,
                    onOpenSupabaseConfig = { showSupabaseConfig = true }
                )
            }
            Role.SCHOOL_ADMIN -> {
                SchoolAdminDashboard(
                    viewModel = viewModel,
                    onOpenSupabaseConfig = { showSupabaseConfig = true }
                )
            }
            Role.TEACHER -> {
                TeacherDashboard(viewModel = viewModel)
            }
            Role.GATE_GUARD -> {
                GuardDashboard(viewModel = viewModel)
            }
            Role.PARENT -> {
                ParentDashboard(viewModel = viewModel)
            }
        }
    }

    if (showSupabaseConfig) {
        SupabaseConfigDialog(
            supabaseManager = viewModel.supabaseManager,
            onDismiss = { showSupabaseConfig = false }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}
