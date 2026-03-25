package com.example.collegeschedule.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.collegeschedule.data.repository.FavoritesRepository
import com.example.collegeschedule.data.repository.ScheduleRepository

class FavoritesViewModelFactory(
    private val favoritesRepository: FavoritesRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(favoritesRepository, scheduleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}