package com.example.collegeschedule.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeschedule.data.repository.FavoritesRepository
import com.example.collegeschedule.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _favoriteGroups = MutableStateFlow<List<String>>(emptyList())
    val favoriteGroups: StateFlow<List<String>> = _favoriteGroups.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favoritesRepository.favoritesFlow.collect { favoritesSet ->
                _favoriteGroups.value = favoritesSet.toList().sorted()
            }
        }
    }

    fun removeFromFavorites(groupName: String) {
        viewModelScope.launch {
            favoritesRepository.removeFavorite(groupName)
        }
    }

    fun clearAllFavorites() {
        viewModelScope.launch {
            _favoriteGroups.value.forEach { group ->
                favoritesRepository.removeFavorite(group)
            }
        }
    }

    fun toggleFavorite(groupName: String, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val isNowFavorite = favoritesRepository.toggleFavorite(groupName)
            onComplete?.invoke(isNowFavorite)
        }
    }

    suspend fun isFavorite(groupName: String): Boolean {
        return favoritesRepository.isFavorite(groupName)
    }
}