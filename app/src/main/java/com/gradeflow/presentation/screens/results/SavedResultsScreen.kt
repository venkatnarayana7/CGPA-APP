package com.gradeflow.presentation.screens.results

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gradeflow.domain.model.CgpaResult
import com.gradeflow.domain.model.TgpaResult
import com.gradeflow.presentation.components.*
import com.gradeflow.presentation.viewmodel.ResultsViewModel
import com.gradeflow.utils.PdfExportUtil
import com.gradeflow.utils.formatGpa
import com.gradeflow.utils.toFormattedDate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedResultsScreen(onBack: () -> Unit, viewModel: ResultsViewModel = hiltViewModel()) {
    val tgpaResults by viewModel.tgpaResults.collectAsStateWithLifecycle()
    val cgpaResults by viewModel.cgpaResults.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Results", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } },
                actions = {
                    if (tgpaResults.isNotEmpty() || cgpaResults.isNotEmpty()) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Outlined.DeleteSweep, "Delete All")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.background) {
                Tab(selectedTab == 0, onClick = { viewModel.selectTab(0) }, text = { Text("TGPA (${tgpaResults.size})") })
                Tab(selectedTab == 1, onClick = { viewModel.selectTab(1) }, text = { Text("CGPA (${cgpaResults.size})") })
            }

            when (selectedTab) {
                0 -> {
                    if (tgpaResults.isEmpty()) {
                        EmptyStateView(Icons.Filled.History, "No TGPA Results", "Calculate your first TGPA")
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(tgpaResults, key = { it.id }) { r ->
                                TgpaResultCard(
                                    result = r,
                                    onDelete = { viewModel.deleteTgpaResult(r.id) },
                                    onExport = {
                                        scope.launch {
                                            isExporting = true
                                            try {
                                                val file = PdfExportUtil.exportTgpaToPdf(context, r)
                                                PdfExportUtil.sharePdf(context, file)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                            isExporting = false
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    if (cgpaResults.isEmpty()) {
                        EmptyStateView(Icons.Filled.History, "No CGPA Results", "Calculate your first CGPA")
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(cgpaResults, key = { it.id }) { r ->
                                CgpaResultCard(
                                    result = r,
                                    onDelete = { viewModel.deleteCgpaResult(r.id) },
                                    onExport = {
                                        scope.launch {
                                            isExporting = true
                                            try {
                                                val file = PdfExportUtil.exportCgpaToPdf(context, r)
                                                PdfExportUtil.sharePdf(context, file)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                            isExporting = false
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete All Results") },
                text = { Text("Are you sure? This cannot be undone.") },
                confirmButton = { TextButton(onClick = { viewModel.deleteAllResults(); showDeleteDialog = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) } },
                dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
            )
        }
    }
}

@Composable
private fun TgpaResultCard(result: TgpaResult, onDelete: () -> Unit, onExport: () -> Unit) {
    GradeFlowCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text("TGPA: ${result.tgpa.formatGpa()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(result.universityName, style = MaterialTheme.typography.bodyMedium)
                Text("${result.semesterName} | ${result.percentage.formatGpa()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                Text(result.timestamp.toFormattedDate(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(0.4f))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onExport, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.PictureAsPdf, "Export PDF", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun CgpaResultCard(result: CgpaResult, onDelete: () -> Unit, onExport: () -> Unit) {
    GradeFlowCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text("CGPA: ${result.cgpa.formatGpa()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(result.universityName, style = MaterialTheme.typography.bodyMedium)
                Text("${result.totalSemesters} Semesters | ${result.percentage.formatGpa()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                Text(result.timestamp.toFormattedDate(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(0.4f))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onExport, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.PictureAsPdf, "Export PDF", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
