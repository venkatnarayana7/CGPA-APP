package com.gradeflow.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gradeflow.data.model.UniversityConfig
import com.gradeflow.domain.model.CgpaResult
import com.gradeflow.domain.model.SemesterEntry
import com.gradeflow.domain.model.UiState
import com.gradeflow.domain.repository.UniversityRepository
import com.gradeflow.domain.usecase.CalculateCgpaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SemesterUiState(val id: String = java.util.UUID.randomUUID().toString(), val gpa: String = "", val credits: String = "")

@HiltViewModel
class CgpaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val universityRepository: UniversityRepository,
    private val calculateCgpaUseCase: CalculateCgpaUseCase
) : ViewModel() {
    private val universityId: String = savedStateHandle.get<String>("universityId") ?: ""
    private val _universityConfig = MutableStateFlow<UniversityConfig?>(null)
    val universityConfig: StateFlow<UniversityConfig?> = _universityConfig.asStateFlow()
    private val _semesters = MutableStateFlow(listOf(SemesterUiState()))
    val semesters: StateFlow<List<SemesterUiState>> = _semesters.asStateFlow()
    private val _result = MutableStateFlow<UiState<CgpaResult>>(UiState.Empty)
    val result: StateFlow<UiState<CgpaResult>> = _result.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    init { viewModelScope.launch { _universityConfig.value = universityRepository.getUniversityConfig(universityId) } }

    fun addSemester() { _semesters.value = _semesters.value + SemesterUiState() }
    fun removeSemester(index: Int) { if (_semesters.value.size > 1) _semesters.value = _semesters.value.toMutableList().apply { removeAt(index) } }
    fun updateSemesterGpa(index: Int, gpa: String) { _semesters.value = _semesters.value.toMutableList().apply { this[index] = this[index].copy(gpa = gpa) } }
    fun updateSemesterCredits(index: Int, credits: String) { _semesters.value = _semesters.value.toMutableList().apply { this[index] = this[index].copy(credits = credits) } }

    fun calculateCgpa() { viewModelScope.launch {
        _result.value = UiState.Loading; _errorMessage.value = null; _isSaved.value = false
        val entries = _semesters.value.mapIndexed { i, s -> SemesterEntry(s.id, i + 1, "Semester ${i + 1}", s.gpa.toDoubleOrNull() ?: 0.0, s.credits.toIntOrNull() ?: 0) }
        calculateCgpaUseCase.calculate(universityId, entries).fold(
            onSuccess = { _result.value = UiState.Success(it) },
            onFailure = { _errorMessage.value = it.message; _result.value = UiState.Empty }
        )
    }}

    fun saveResult() { viewModelScope.launch { (_result.value as? UiState.Success)?.data?.let { calculateCgpaUseCase.saveResult(it); _isSaved.value = true } } }
    fun clearError() { _errorMessage.value = null }
    fun reset() { _semesters.value = listOf(SemesterUiState()); _result.value = UiState.Empty; _isSaved.value = false; _errorMessage.value = null }
}
