package com.gradeflow.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gradeflow.domain.model.CgpaResult
import com.gradeflow.domain.model.TgpaResult
import com.gradeflow.domain.repository.ResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HomeLayoutMode { FLOW, GRID }

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val resultRepository: ResultRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("gradeflow_prefs", Context.MODE_PRIVATE)

    private val _recentTgpaResults = MutableStateFlow<List<TgpaResult>>(emptyList())
    val recentTgpaResults: StateFlow<List<TgpaResult>> = _recentTgpaResults.asStateFlow()

    private val _recentCgpaResults = MutableStateFlow<List<CgpaResult>>(emptyList())
    val recentCgpaResults: StateFlow<List<CgpaResult>> = _recentCgpaResults.asStateFlow()

    private val _layoutMode = MutableStateFlow(
        if (prefs.getString("home_layout", "flow") == "grid") HomeLayoutMode.GRID else HomeLayoutMode.FLOW
    )
    val layoutMode: StateFlow<HomeLayoutMode> = _layoutMode.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) { resultRepository.getAllTgpaResults().collect { _recentTgpaResults.value = it.take(3) } }
        viewModelScope.launch(Dispatchers.IO) { resultRepository.getAllCgpaResults().collect { _recentCgpaResults.value = it.take(3) } }
    }

    fun toggleLayoutMode() {
        val newMode = if (_layoutMode.value == HomeLayoutMode.FLOW) HomeLayoutMode.GRID else HomeLayoutMode.FLOW
        _layoutMode.value = newMode
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit().putString("home_layout", if (newMode == HomeLayoutMode.GRID) "grid" else "flow").apply()
        }
    }
}
