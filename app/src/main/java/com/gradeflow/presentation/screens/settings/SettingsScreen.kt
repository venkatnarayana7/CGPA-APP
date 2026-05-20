package com.gradeflow.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var darkMode by remember { mutableStateOf(false) }
    Scaffold(topBar = { TopAppBar(title = { Text("Settings", fontWeight = FontWeight.SemiBold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text("Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
            Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.DarkMode, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(12.dp)); Column { Text("Dark Mode", style = MaterialTheme.typography.bodyLarge); Text("Follow system theme", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f)) } }
                    Switch(checked = darkMode, onCheckedChange = { darkMode = it })
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
            Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column { ListItem(headlineContent = { Text("Export Results") }, supportingContent = { Text("Export as PDF") }, leadingContent = { Icon(Icons.Filled.FileDownload, null, tint = MaterialTheme.colorScheme.primary) }); HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f)); ListItem(headlineContent = { Text("Clear All Data") }, supportingContent = { Text("Remove all saved results") }, leadingContent = { Icon(Icons.Filled.DeleteForever, null, tint = MaterialTheme.colorScheme.error) }) }
            }
            Spacer(Modifier.height(24.dp))
            Text("App Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
            Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column { ListItem(headlineContent = { Text("Version") }, supportingContent = { Text("1.0.0") }, leadingContent = { Icon(Icons.Filled.Info, null, tint = MaterialTheme.colorScheme.primary) }); HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f)); ListItem(headlineContent = { Text("Universities") }, supportingContent = { Text("10 universities supported") }, leadingContent = { Icon(Icons.Filled.School, null, tint = MaterialTheme.colorScheme.primary) }) }
            }
        }
    }
}
