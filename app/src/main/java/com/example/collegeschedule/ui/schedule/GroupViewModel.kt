package com.example.collegeschedule.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeschedule.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupViewModel(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _groups = MutableStateFlow<List<String>>(emptyList())
    val groups: StateFlow<List<String>> = _groups.asStateFlow()

    private val _filteredGroups = MutableStateFlow<List<String>>(emptyList())
    val filteredGroups: StateFlow<List<String>> = _filteredGroups.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadAllGroups()
    }

    fun loadAllGroups() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val groupList = repository.getAllGroups()
                _groups.value = groupList
                _filteredGroups.value = groupList
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки групп"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchGroups(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _filteredGroups.value = _groups.value
            } else {
                _isLoading.value = true
                try {
                    val results = repository.searchGroups(query)
                    _filteredGroups.value = results
                } catch (e: Exception) {
                    _error.value = e.message ?: "Ошибка поиска групп"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun refreshGroups() {
        loadAllGroups()
    }
}