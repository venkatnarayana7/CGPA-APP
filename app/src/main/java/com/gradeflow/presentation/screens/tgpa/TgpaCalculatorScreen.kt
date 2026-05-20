package com.gradeflow.presentation.screens.tgpa

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
import com.gradeflow.domain.model.UiState
import com.gradeflow.presentation.components.*
import com.gradeflow.presentation.theme.InputFieldShape
import com.gradeflow.presentation.viewmodel.TgpaViewModel
import com.gradeflow.utils.formatGpa

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TgpaCalculatorScreen(universityId: String, onBack: () -> Unit, onNavigateToHome: () -> Unit, viewModel: TgpaViewModel = hiltViewModel()) {
    val config by viewModel.universityConfig.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val semesterName by viewModel.semesterName.collectAsState()
    val result by viewModel.result.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) { errorMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() } }
    LaunchedEffect(isSaved) { if (isSaved) snackbarHostState.showSnackbar("Result saved!") }

    Scaffold(topBar = { TopAppBar(title = { Column { Text("TGPA Calculator", fontWeight = FontWeight.SemiBold); config?.let { Text(it.shortName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(0.6f)) } } }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }, actions = { IconButton(onClick = { viewModel.reset() }) { Icon(Icons.Filled.Refresh, "Reset") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = { ExtendedFloatingActionButton(onClick = { viewModel.calculateTgpa() }, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) { Icon(Icons.Filled.Calculate, null); Spacer(Modifier.width(8.dp)); Text("Calculate") } }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 100.dp)) {
            item { Spacer(Modifier.height(8.dp)); OutlinedTextField(value = semesterName, onValueChange = { viewModel.updateSemesterName(it) }, label = { Text("Semester Name") }, modifier = Modifier.fillMaxWidth(), shape = InputFieldShape, singleLine = true) }
            item { AnimatedVisibility(result is UiState.Success, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                (result as? UiState.Success)?.data?.let { Column { GpaResultCard("Your TGPA", it.tgpa.formatGpa(), it.percentage.formatGpa(), "${it.totalSubjects} subjects | ${it.totalCredits} credits"); Spacer(Modifier.height(8.dp)); GradeFlowOutlinedButton(if (isSaved) "Saved!" else "Save", onClick = { viewModel.saveResult() }, icon = if (isSaved) Icons.Filled.Check else Icons.Filled.Save) } }
            }}
            item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("Subjects (${subjects.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold); TextButton(onClick = { viewModel.addSubject() }) { Icon(Icons.Filled.Add, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Add") } } }
            itemsIndexed(subjects, key = { _, s -> s.id }) { i, s -> SubjectInputCard(i, s.name, s.credits, s.grade, config?.gradeMapping?.map { it.grade } ?: emptyList(), onNameChange = { viewModel.updateSubjectName(i, it) }, onCreditsChange = { viewModel.updateSubjectCredits(i, it) }, onGradeChange = { viewModel.updateSubjectGrade(i, it) }, onRemove = { viewModel.removeSubject(i) }) }
            item { GradeFlowOutlinedButton("Add Another Subject", onClick = { viewModel.addSubject() }, icon = Icons.Filled.Add) }
        }
    }
}
