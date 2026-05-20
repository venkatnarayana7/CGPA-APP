package com.gradeflow.presentation.screens.cgpa

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gradeflow.domain.model.UiState
import com.gradeflow.presentation.components.*
import com.gradeflow.presentation.viewmodel.CgpaViewModel
import com.gradeflow.utils.formatGpa

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CgpaCalculatorScreen(
    universityId: String,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: CgpaViewModel = hiltViewModel()
) {
    val config by viewModel.universityConfig.collectAsStateWithLifecycle()
    val semesters by viewModel.semesters.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val gradingScale = remember(config) { config?.gradingScale ?: 10 }

    LaunchedEffect(errorMessage) { errorMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() } }
    LaunchedEffect(isSaved) { if (isSaved) snackbarHostState.showSnackbar("Result saved!") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("CGPA Calculator", fontWeight = FontWeight.SemiBold)
                        config?.let { Text(it.shortName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(0.6f)) }
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } },
                actions = { IconButton(onClick = { viewModel.reset() }) { Icon(Icons.Filled.Refresh, "Reset") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.calculateCgpa() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) { Icon(Icons.Filled.Calculate, null); Spacer(Modifier.width(8.dp)); Text("Calculate") }
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item(key = "result_card") {
                Spacer(Modifier.height(8.dp))
                AnimatedVisibility(result is UiState.Success, enter = fadeIn(), exit = fadeOut()) {
                    (result as? UiState.Success)?.data?.let {
                        Column {
                            GpaResultCard("Your CGPA", it.cgpa.formatGpa(), it.percentage.formatGpa(), "${it.totalSemesters} semesters | ${it.totalCredits} credits")
                            Spacer(Modifier.height(8.dp))
                            GradeFlowOutlinedButton(if (isSaved) "Saved!" else "Save", onClick = { viewModel.saveResult() }, icon = if (isSaved) Icons.Filled.Check else Icons.Filled.Save)
                        }
                    }
                }
            }

            item(key = "header") {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Semesters (${semesters.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = { viewModel.addSemester() }) { Icon(Icons.Filled.Add, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Add") }
                }
            }

            itemsIndexed(semesters, key = { _, s -> s.id }) { i, s ->
                SemesterInputCard(
                    index = i, gpa = s.gpa, credits = s.credits, gradingScale = gradingScale,
                    onGpaChange = { viewModel.updateSemesterGpa(i, it) },
                    onCreditsChange = { viewModel.updateSemesterCredits(i, it) },
                    onRemove = { viewModel.removeSemester(i) }
                )
            }

            item(key = "add_btn") {
                GradeFlowOutlinedButton("Add Another Semester", onClick = { viewModel.addSemester() }, icon = Icons.Filled.Add)
            }
        }
    }
}
