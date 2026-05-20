package com.gradeflow.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gradeflow.presentation.components.FeatureCard
import com.gradeflow.presentation.components.GradeFlowCard
import com.gradeflow.presentation.viewmodel.HomeViewModel
import com.gradeflow.utils.formatGpa
import com.gradeflow.utils.toShortDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToUniversitySelection: (String) -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val recentTgpa by viewModel.recentTgpaResults.collectAsStateWithLifecycle()
    val recentCgpa by viewModel.recentCgpaResults.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("GradeFlow", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Your GPA Companion", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(0.6f))
                    }
                },
                actions = { IconButton(onClick = onNavigateToSettings) { Icon(Icons.Outlined.Settings, "Settings") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text("Calculators", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
            FeatureCard("TGPA Calculator", "Calculate Term Grade Point Average", Icons.Filled.Calculate, onClick = { onNavigateToUniversitySelection("tgpa") })
            Spacer(Modifier.height(12.dp))
            FeatureCard("CGPA Calculator", "Calculate Cumulative Grade Point Average", Icons.Filled.Functions, onClick = { onNavigateToUniversitySelection("cgpa") })
            Spacer(Modifier.height(24.dp))
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
            FeatureCard("Saved Results", "View your calculation history", Icons.Filled.History, onClick = onNavigateToResults)
            Spacer(Modifier.height(12.dp))
            FeatureCard("About", "App information", Icons.Filled.Info, onClick = onNavigateToAbout)
            Spacer(Modifier.height(24.dp))

            if (recentTgpa.isNotEmpty() || recentCgpa.isNotEmpty()) {
                Text("Recent Results", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
                recentTgpa.forEach { r ->
                    key(r.id) {
                        GradeFlowCard(Modifier.padding(vertical = 4.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text("CGPA: ${r.tgpa.formatGpa()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("${r.universityName} - ${r.semesterName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    r.timestamp.toShortDate(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.45f),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
                recentCgpa.forEach { r ->
                    key(r.id) {
                        GradeFlowCard(Modifier.padding(vertical = 4.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text("CGPA: ${r.cgpa.formatGpa()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("${r.universityName} - ${r.totalSemesters} Semesters", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    r.timestamp.toShortDate(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.45f),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
