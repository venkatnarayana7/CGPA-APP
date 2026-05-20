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
class HomeViewModel @Inject constructor(private val resultRepository: ResultRepository) : ViewModel() {
    private val _recentTgpaResults = MutableStateFlow<List<TgpaResult>>(emptyList())
    val recentTgpaResults: StateFlow<List<TgpaResult>> = _recentTgpaResults.asStateFlow()
    private val _recentCgpaResults = MutableStateFlow<List<CgpaResult>>(emptyList())
    val recentCgpaResults: StateFlow<List<CgpaResult>> = _recentCgpaResults.asStateFlow()

    init {
        viewModelScope.launch { resultRepository.getAllTgpaResults().collect { _recentTgpaResults.value = it.take(3) } }
        viewModelScope.launch { resultRepository.getAllCgpaResults().collect { _recentCgpaResults.value = it.take(3) } }
    }
}
