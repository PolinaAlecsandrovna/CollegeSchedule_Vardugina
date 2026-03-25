package com.example.collegeschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.api.ScheduleApi
import com.example.collegeschedule.data.repository.FavoritesRepository
import com.example.collegeschedule.data.repository.ScheduleRepository
import com.example.collegeschedule.ui.favorites.FavoritesScreen
import com.example.collegeschedule.ui.schedule.ScheduleScreen
import com.example.collegeschedule.ui.theme.CollegeScheduleTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CollegeScheduleTheme {
                CollegeScheduleApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun CollegeScheduleApp() {
    var currentDestination by rememberSaveable {
        mutableStateOf(AppDestinations.HOME)
    }
    var selectedGroup by remember { mutableStateOf("ИС-12") }

    val context = androidx.compose.ui.platform.LocalContext.current
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5268/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api = remember { retrofit.create(ScheduleApi::class.java) }
    val repository = remember { ScheduleRepository(api) }
    val favoritesRepository = remember { FavoritesRepository(context) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                AppDestinations.HOME -> ScheduleScreen(
                    initialGroup = selectedGroup,
                    onGroupChange = { newGroup ->
                        selectedGroup = newGroup
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                AppDestinations.FAVORITES -> FavoritesScreen(
                    onGroupSelected = { group ->
                        selectedGroup = group
                        currentDestination = AppDestinations.HOME
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                AppDestinations.PROFILE -> ProfileScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Профиль",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Студент колледжа",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Приложение для просмотра расписания",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "О приложении",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Версия 1.0.0\n\nДанные загружаются из базы данных колледжа в реальном времени.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Расписание", Icons.Default.Home),
    FAVORITES("Избранное", Icons.Default.Favorite),
    PROFILE("Профиль", Icons.Default.AccountBox),
}