package com.example.collegeschedule.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.collegeschedule.data.repository.ScheduleRepository

class GroupViewModelFactory(
    private val repository: ScheduleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}