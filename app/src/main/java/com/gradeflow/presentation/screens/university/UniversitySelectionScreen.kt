package com.gradeflow.presentation.screens.university

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gradeflow.domain.model.UiState
import com.gradeflow.presentation.components.*
import com.gradeflow.presentation.viewmodel.UniversityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversitySelectionScreen(calculatorType: String, onUniversitySelected: (String) -> Unit, onBack: () -> Unit, viewModel: UniversityViewModel = hiltViewModel()) {
    val universities by viewModel.universities.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Select University", fontWeight = FontWeight.SemiBold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            GradeFlowSearchBar(query = searchQuery, onQueryChange = { viewModel.searchUniversities(it) }, placeholder = "Search universities...")
            Spacer(Modifier.height(16.dp))
            when (val state = universities) {
                is UiState.Loading -> LoadingView(message = "Loading universities...")
                is UiState.Empty -> EmptyStateView(title = "No universities found", subtitle = "Try a different search term")
                is UiState.Error -> EmptyStateView(title = "Error", subtitle = state.message)
                is UiState.Success -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(state.data, key = { it.id }) { u -> UniversityCard(u.name, u.shortName, u.location, u.isFavorite, onClick = { onUniversitySelected(u.id) }, onFavoriteClick = { viewModel.toggleFavorite(u) }) }
                }
            }
        }
    }
}
