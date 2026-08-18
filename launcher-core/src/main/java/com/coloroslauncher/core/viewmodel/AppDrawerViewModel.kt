package com.coloroslauncher.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.coloroslauncher.core.model.AppInfo
import com.coloroslauncher.core.model.LaunchSource
import com.coloroslauncher.core.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class AppDrawerUiState(
    val allApps: List<AppInfo> = emptyList(),
    val query: String = "",
) {
    val filteredApps: List<AppInfo>
        get() = if (query.isBlank()) {
            allApps.sortedBy { it.label.lowercase() }
        } else {
            allApps
                .filter { it.label.contains(query, ignoreCase = true) }
                .sortedBy { it.label.lowercase() }
        }
}

class AppDrawerViewModel(private val appRepository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AppDrawerUiState())
    val uiState: StateFlow<AppDrawerUiState> = _uiState.asStateFlow()

    init {
        refresh()
        appRepository.observeAppListChanges().onEach { refresh() }.launchIn(viewModelScope)
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(allApps = appRepository.loadInstalledApps())
        }
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun launchApp(app: AppInfo, source: LaunchSource? = null) {
        appRepository.launch(app, source)
    }

    class Factory(private val appRepository: AppRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AppDrawerViewModel(appRepository) as T
        }
    }
}
