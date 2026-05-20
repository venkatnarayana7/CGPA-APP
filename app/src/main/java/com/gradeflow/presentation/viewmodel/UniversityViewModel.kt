package com.gradeflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gradeflow.domain.model.UiState
import com.gradeflow.domain.model.University
import com.gradeflow.domain.repository.UniversityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UniversityViewModel @Inject constructor(
    private val universityRepository: UniversityRepository
) : ViewModel() {

    private val _universities = MutableStateFlow<UiState<List<University>>>(UiState.Loading)
    val universities: StateFlow<UiState<List<University>>> = _universities.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init { loadUniversities() }

    fun loadUniversities() {
        viewModelScope.launch {
            _universities.value = UiState.Loading
            try {
                val result = withContext(Dispatchers.IO) {
                    universityRepository.getAllUniversities()
                }
                _universities.value = if (result.isEmpty()) UiState.Empty else UiState.Success(result)
            } catch (e: Exception) {
                _universities.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    fun searchUniversities(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    universityRepository.searchUniversities(query)
                }
                _universities.value = if (result.isEmpty()) UiState.Empty else UiState.Success(result)
            } catch (e: Exception) {
                _universities.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    fun toggleFavorite(university: University) {
        viewModelScope.launch(Dispatchers.IO) {
            universityRepository.toggleFavorite(university.id, university.name, university.shortName)
            withContext(Dispatchers.Main) {
                if (_searchQuery.value.isBlank()) loadUniversities() else searchUniversities(_searchQuery.value)
            }
        }
    }
}
