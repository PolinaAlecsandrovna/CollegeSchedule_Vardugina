package com.example.collegeschedule.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.data.repository.ScheduleRepository
import com.example.collegeschedule.ui.schedule.GroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSearch(
    selectedGroup: String,
    onGroupSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { ScheduleRepository(RetrofitInstance.api) }
    val viewModel: GroupViewModel = viewModel(
        factory = GroupViewModelFactory(repository)
    )

    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(selectedGroup) }

    val groups by viewModel.filteredGroups.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(searchText) {
        viewModel.searchGroups(searchText)
    }

    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
                if (expanded && groups.isEmpty() && error == null) {
                    viewModel.refreshGroups()
                }
            }
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    expanded = true
                },
                label = { Text("Поиск группы") },
                placeholder = { Text("Введите название группы...") },
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                },
                supportingText = {
                    if (error != null) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = error != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded && groups.isNotEmpty(),
                onDismissRequest = { expanded = false }
            ) {
                groups.forEach { group ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                group,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            searchText = group
                            onGroupSelected(group)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (!expanded && searchText.isNotBlank() && groups.isNotEmpty()) {
            Text(
                text = "Найдено ${groups.size} групп",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

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