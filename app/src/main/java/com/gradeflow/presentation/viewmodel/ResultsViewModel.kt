package com.gradeflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gradeflow.domain.model.CgpaResult
import com.gradeflow.domain.model.TgpaResult
import com.gradeflow.domain.repository.ResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(private val resultRepository: ResultRepository) : ViewModel() {
    private val _tgpaResults = MutableStateFlow<List<TgpaResult>>(emptyList())
    val tgpaResults: StateFlow<List<TgpaResult>> = _tgpaResults.asStateFlow()
    private val _cgpaResults = MutableStateFlow<List<CgpaResult>>(emptyList())
    val cgpaResults: StateFlow<List<CgpaResult>> = _cgpaResults.asStateFlow()
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    init {
        viewModelScope.launch { resultRepository.getAllTgpaResults().collect { _tgpaResults.value = it } }
        viewModelScope.launch { resultRepository.getAllCgpaResults().collect { _cgpaResults.value = it } }
    }

    fun selectTab(tab: Int) { _selectedTab.value = tab }
    fun deleteTgpaResult(id: Long) { viewModelScope.launch { resultRepository.deleteTgpaResult(id) } }
    fun deleteCgpaResult(id: Long) { viewModelScope.launch { resultRepository.deleteCgpaResult(id) } }
    fun deleteAllResults() { viewModelScope.launch { resultRepository.deleteAllTgpaResults(); resultRepository.deleteAllCgpaResults() } }
}
