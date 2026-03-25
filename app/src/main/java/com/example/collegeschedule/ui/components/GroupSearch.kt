package com.example.collegeschedule.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.data.repository.FavoritesRepository
import com.example.collegeschedule.data.repository.ScheduleRepository
import com.example.collegeschedule.ui.favorites.FavoritesViewModel
import com.example.collegeschedule.ui.schedule.GroupViewModel
import com.example.collegeschedule.ui.schedule.GroupViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSearch(
    selectedGroup: String,
    onGroupSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    favoritesViewModel: FavoritesViewModel? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { ScheduleRepository(RetrofitInstance.api) }
    val favoritesRepo = remember { FavoritesRepository(context) }
    val viewModel: GroupViewModel = viewModel(
        factory = GroupViewModelFactory(repository)
    )
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(selectedGroup) }
    var isFavorite by remember { mutableStateOf(false) }

    val groups by viewModel.filteredGroups.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(selectedGroup, favoritesViewModel) {
        if (favoritesViewModel != null) {
            isFavorite = favoritesViewModel.isFavorite(selectedGroup)
        }
    }

    LaunchedEffect(searchText) {
        viewModel.searchGroups(searchText)
    }

    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                    if (expanded && groups.isEmpty() && error == null) {
                        viewModel.refreshGroups()
                    }
                },
                modifier = Modifier.weight(1f)
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
                        var isGroupFavorite by remember { mutableStateOf(false) }

                        LaunchedEffect(group, favoritesViewModel) {
                            if (favoritesViewModel != null) {
                                isGroupFavorite = favoritesViewModel.isFavorite(group)
                            }
                        }

                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        group,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                    favoritesViewModel?.let { vm ->
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    vm.toggleFavorite(group) { newStatus ->
                                                        isGroupFavorite = newStatus
                                                    }
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                if (isGroupFavorite) Icons.Default.Favorite
                                                else Icons.Default.FavoriteBorder,
                                                contentDescription = if (isGroupFavorite) "Удалить из избранного"
                                                else "Добавить в избранное",
                                                tint = if (isGroupFavorite) MaterialTheme.colorScheme.error
                                                else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
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
            favoritesViewModel?.let { vm ->
                IconButton(
                    onClick = {
                        scope.launch {
                            vm.toggleFavorite(selectedGroup) { newStatus ->
                                isFavorite = newStatus
                            }
                        }
                    },
                    enabled = selectedGroup.isNotBlank()
                ) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                        tint = if (isFavorite) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
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