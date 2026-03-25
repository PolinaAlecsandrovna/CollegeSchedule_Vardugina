package com.example.collegeschedule.ui.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.data.repository.FavoritesRepository
import com.example.collegeschedule.data.repository.ScheduleRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onGroupSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val favoritesRepository = remember { FavoritesRepository(context) }
    val scheduleRepository = remember { ScheduleRepository(RetrofitInstance.api) }

    val viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(favoritesRepository, scheduleRepository)
    )

    val favoriteGroups by viewModel.favoriteGroups.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (favoriteGroups.isNotEmpty()) {
            TopAppBar(
                title = { Text("Избранные группы") },
                actions = {
                    IconButton(
                        onClick = { showClearDialog = true }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Очистить избранное"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            favoriteGroups.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Нет избранных",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Нет избранных групп",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Добавьте группы в избранное, чтобы быстро переключаться между ними",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoriteGroups) { group ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onGroupSelected(group) },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = group,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Нажмите для просмотра расписания",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.removeFromFavorites(group)
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Удалить из избранного",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Очистить избранное") },
            text = { Text("Вы уверены что хотите удалить все группы из избранного?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllFavorites()
                        showClearDialog = false
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

