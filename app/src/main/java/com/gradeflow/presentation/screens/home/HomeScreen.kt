package com.gradeflow.presentation.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gradeflow.presentation.components.FeatureCard
import com.gradeflow.presentation.components.GradeFlowCard
import com.gradeflow.presentation.viewmodel.HomeLayoutMode
import com.gradeflow.presentation.viewmodel.HomeViewModel
import com.gradeflow.utils.formatGpa
import com.gradeflow.utils.toShortDate

data class GridToolItem(
    val title: String,
    val subtitle: String = "",
    val icon: ImageVector,
    val action: String // navigation action key
)

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
    val layoutMode by viewModel.layoutMode.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("GradeFlow", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Your GPA Companion", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(0.6f))
                    }
                },
                actions = {
                    // Layout toggle
                    IconButton(onClick = { viewModel.toggleLayoutMode() }) {
                        Icon(
                            if (layoutMode == HomeLayoutMode.FLOW) Icons.Outlined.GridView else Icons.Outlined.ViewAgenda,
                            contentDescription = "Toggle View"
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Outlined.Settings, "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        when (layoutMode) {
            HomeLayoutMode.FLOW -> FlowViewContent(
                modifier = Modifier.padding(padding),
                onNavigateToUniversitySelection = onNavigateToUniversitySelection,
                onNavigateToResults = onNavigateToResults,
                onNavigateToAbout = onNavigateToAbout,
                recentTgpa = recentTgpa,
                recentCgpa = recentCgpa
            )
            HomeLayoutMode.GRID -> CompactGridContent(
                modifier = Modifier.padding(padding),
                onNavigateToUniversitySelection = onNavigateToUniversitySelection,
                onNavigateToResults = onNavigateToResults,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToAbout = onNavigateToAbout
            )
        }
    }
}

// ==================== FLOW VIEW (DEFAULT) ====================

@Composable
private fun FlowViewContent(
    modifier: Modifier,
    onNavigateToUniversitySelection: (String) -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToAbout: () -> Unit,
    recentTgpa: List<com.gradeflow.domain.model.TgpaResult>,
    recentCgpa: List<com.gradeflow.domain.model.CgpaResult>
) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
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
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                Text("TGPA: ${r.tgpa.formatGpa()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${r.universityName} - ${r.semesterName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(r.timestamp.toShortDate(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(0.45f), modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
            recentCgpa.forEach { r ->
                key(r.id) {
                    GradeFlowCard(Modifier.padding(vertical = 4.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                Text("CGPA: ${r.cgpa.formatGpa()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${r.universityName} - ${r.totalSemesters} Semesters", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(r.timestamp.toShortDate(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(0.45f), modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

// ==================== COMPACT GRID VIEW ====================

@Composable
private fun CompactGridContent(
    modifier: Modifier,
    onNavigateToUniversitySelection: (String) -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val gridItems = remember {
        listOf(
            // GPA Section
            GridToolItem("TGPA", "Term GPA", Icons.Filled.Calculate, "tgpa"),
            GridToolItem("CGPA", "Cumulative GPA", Icons.Filled.Functions, "cgpa"),
            // Academic Tools
            GridToolItem("GPA to %", "Convert to percentage", Icons.Filled.Percent, "tgpa"),
            GridToolItem("University", "Search universities", Icons.Filled.School, "tgpa"),
            // Results & Export
            GridToolItem("Saved Results", "View history", Icons.Filled.History, "results"),
            GridToolItem("Export PDF", "Download reports", Icons.Filled.PictureAsPdf, "results"),
            // General
            GridToolItem("Settings", "App preferences", Icons.Filled.Settings, "settings"),
            GridToolItem("About", "App info", Icons.Filled.Info, "about")
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize().padding(horizontal = 12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(gridItems, key = { it.title }) { item ->
            CompactGridCard(
                title = item.title,
                subtitle = item.subtitle,
                icon = item.icon,
                onClick = {
                    when (item.action) {
                        "tgpa" -> onNavigateToUniversitySelection("tgpa")
                        "cgpa" -> onNavigateToUniversitySelection("cgpa")
                        "results" -> onNavigateToResults()
                        "settings" -> onNavigateToSettings()
                        "about" -> onNavigateToAbout()
                    }
                }
            )
        }
    }
}

// ==================== COMPACT GRID CARD COMPONENT ====================

@Composable
private fun CompactGridCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
