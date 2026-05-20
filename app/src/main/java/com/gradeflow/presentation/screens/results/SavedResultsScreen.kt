package com.gradeflow.presentation.screens.results

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gradeflow.presentation.components.*
import com.gradeflow.presentation.viewmodel.ResultsViewModel
import com.gradeflow.utils.formatGpa
import com.gradeflow.utils.toFormattedDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedResultsScreen(onBack: () -> Unit, viewModel: ResultsViewModel = hiltViewModel()) {
    val tgpaResults by viewModel.tgpaResults.collectAsState()
    val cgpaResults by viewModel.cgpaResults.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Saved Results", fontWeight = FontWeight.SemiBold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }, actions = { if (tgpaResults.isNotEmpty() || cgpaResults.isNotEmpty()) IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Outlined.DeleteSweep, "Delete All") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.background) {
                Tab(selectedTab == 0, onClick = { viewModel.selectTab(0) }, text = { Text("TGPA (${tgpaResults.size})") })
                Tab(selectedTab == 1, onClick = { viewModel.selectTab(1) }, text = { Text("CGPA (${cgpaResults.size})") })
            }
            when (selectedTab) {
                0 -> if (tgpaResults.isEmpty()) EmptyStateView(Icons.Filled.History, "No TGPA Results", "Calculate your first TGPA") else LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tgpaResults, key = { it.id }) { r -> GradeFlowCard { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Column(Modifier.weight(1f)) { Text("TGPA: ${r.tgpa.formatGpa()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text(r.universityName, style = MaterialTheme.typography.bodyMedium); Text("${r.semesterName} | ${r.percentage.formatGpa()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f)); Text(r.timestamp.toFormattedDate(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.4f)) }; IconButton(onClick = { viewModel.deleteTgpaResult(r.id) }) { Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error) } } } }
                }
                1 -> if (cgpaResults.isEmpty()) EmptyStateView(Icons.Filled.History, "No CGPA Results", "Calculate your first CGPA") else LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cgpaResults, key = { it.id }) { r -> GradeFlowCard { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Column(Modifier.weight(1f)) { Text("CGPA: ${r.cgpa.formatGpa()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text(r.universityName, style = MaterialTheme.typography.bodyMedium); Text("${r.totalSemesters} Semesters | ${r.percentage.formatGpa()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f)); Text(r.timestamp.toFormattedDate(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.4f)) }; IconButton(onClick = { viewModel.deleteCgpaResult(r.id) }) { Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error) } } } }
                }
            }
        }
        if (showDeleteDialog) AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text("Delete All Results") }, text = { Text("Are you sure? This cannot be undone.") }, confirmButton = { TextButton(onClick = { viewModel.deleteAllResults(); showDeleteDialog = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) } }, dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } })
    }
}
