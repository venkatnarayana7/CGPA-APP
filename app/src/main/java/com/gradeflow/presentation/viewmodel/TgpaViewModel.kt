package com.gradeflow.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gradeflow.data.model.UniversityConfig
import com.gradeflow.domain.model.SubjectEntry
import com.gradeflow.domain.model.TgpaResult
import com.gradeflow.domain.model.UiState
import com.gradeflow.domain.repository.UniversityRepository
import com.gradeflow.domain.usecase.CalculateTgpaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubjectUiState(val id: String = java.util.UUID.randomUUID().toString(), val name: String = "", val credits: String = "", val grade: String = "")

@HiltViewModel
class TgpaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val universityRepository: UniversityRepository,
    private val calculateTgpaUseCase: CalculateTgpaUseCase
) : ViewModel() {
    private val universityId: String = savedStateHandle.get<String>("universityId") ?: ""
    private val _universityConfig = MutableStateFlow<UniversityConfig?>(null)
    val universityConfig: StateFlow<UniversityConfig?> = _universityConfig.asStateFlow()
    private val _subjects = MutableStateFlow(listOf(SubjectUiState()))
    val subjects: StateFlow<List<SubjectUiState>> = _subjects.asStateFlow()
    private val _semesterName = MutableStateFlow("Semester 1")
    val semesterName: StateFlow<String> = _semesterName.asStateFlow()
    private val _result = MutableStateFlow<UiState<TgpaResult>>(UiState.Empty)
    val result: StateFlow<UiState<TgpaResult>> = _result.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    init { viewModelScope.launch { _universityConfig.value = universityRepository.getUniversityConfig(universityId) } }

    fun updateSemesterName(name: String) { _semesterName.value = name }
    fun addSubject() { _subjects.value = _subjects.value + SubjectUiState() }
    fun removeSubject(index: Int) { if (_subjects.value.size > 1) _subjects.value = _subjects.value.toMutableList().apply { removeAt(index) } }
    fun updateSubjectName(index: Int, name: String) { _subjects.value = _subjects.value.toMutableList().apply { this[index] = this[index].copy(name = name) } }
    fun updateSubjectCredits(index: Int, credits: String) { _subjects.value = _subjects.value.toMutableList().apply { this[index] = this[index].copy(credits = credits) } }
    fun updateSubjectGrade(index: Int, grade: String) { _subjects.value = _subjects.value.toMutableList().apply { this[index] = this[index].copy(grade = grade) } }

    fun calculateTgpa() { viewModelScope.launch {
        _result.value = UiState.Loading; _errorMessage.value = null; _isSaved.value = false
        val entries = _subjects.value.map { SubjectEntry(it.id, it.name.ifBlank { "Subject" }, it.credits.toIntOrNull() ?: 0, it.grade) }
        calculateTgpaUseCase.calculate(universityId, _semesterName.value, entries).fold(
            onSuccess = { _result.value = UiState.Success(it) },
            onFailure = { _errorMessage.value = it.message; _result.value = UiState.Empty }
        )
    }}

    fun saveResult() { viewModelScope.launch { (_result.value as? UiState.Success)?.data?.let { calculateTgpaUseCase.saveResult(it); _isSaved.value = true } } }
    fun clearError() { _errorMessage.value = null }
    fun reset() { _subjects.value = listOf(SubjectUiState()); _result.value = UiState.Empty; _isSaved.value = false; _errorMessage.value = null }
}
