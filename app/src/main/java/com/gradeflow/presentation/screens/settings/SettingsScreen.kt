package com.gradeflow.presentation.screens.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gradeflow.presentation.viewmodel.ResultsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    resultsViewModel: ResultsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("gradeflow_prefs", 0) }
    var darkMode by remember { mutableStateOf(prefs.getBoolean("dark_mode", false)) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text("Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
            Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DarkMode, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
                            Text(if (darkMode) "Enabled" else "Disabled", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                        }
                    }
                    Switch(checked = darkMode, onCheckedChange = {
                        darkMode = it
                        prefs.edit().putBoolean("dark_mode", it).apply()
                        Toast.makeText(context, if (it) "Dark mode enabled. Restart app to apply." else "Light mode enabled. Restart app to apply.", Toast.LENGTH_SHORT).show()
                    })
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
            Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column {
                    ListItem(headlineContent = { Text("Clear All Data") }, supportingContent = { Text("Remove all saved results permanently") }, leadingContent = { Icon(Icons.Filled.DeleteForever, null, tint = MaterialTheme.colorScheme.error) }, modifier = Modifier.clickable { showDeleteDialog = true })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                    ListItem(headlineContent = { Text("Reset Onboarding") }, supportingContent = { Text("Show onboarding screen on next launch") }, leadingContent = { Icon(Icons.Filled.Refresh, null, tint = MaterialTheme.colorScheme.primary) }, modifier = Modifier.clickable {
                        prefs.edit().putBoolean("onboarding_completed", false).apply()
                        Toast.makeText(context, "Onboarding will show on next launch", Toast.LENGTH_SHORT).show()
                    })
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("App Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
            Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column {
                    ListItem(headlineContent = { Text("Version") }, supportingContent = { Text("1.0.0") }, leadingContent = { Icon(Icons.Filled.Info, null, tint = MaterialTheme.colorScheme.primary) })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                    ListItem(headlineContent = { Text("Universities Supported") }, supportingContent = { Text("10 universities") }, leadingContent = { Icon(Icons.Filled.School, null, tint = MaterialTheme.colorScheme.primary) })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                    ListItem(headlineContent = { Text("Developer") }, supportingContent = { Text("Venkata Narayana") }, leadingContent = { Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.primary) })
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Clear All Data") },
                text = { Text("This will permanently delete all your saved TGPA and CGPA results. This action cannot be undone.") },
                confirmButton = { TextButton(onClick = { resultsViewModel.deleteAllResults(); showDeleteDialog = false; Toast.makeText(context, "All data cleared", Toast.LENGTH_SHORT).show() }) { Text("Delete All", color = MaterialTheme.colorScheme.error) } },
                dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
            )
        }
    }
}
