package com.example.collegeschedule.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites")

class FavoritesRepository(private val context: Context) {

    companion object {
        private val FAVORITES_KEY = stringSetPreferencesKey("favorite_groups")
    }

    val favoritesFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITES_KEY] ?: emptySet()
        }

    suspend fun addFavorite(groupName: String) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY] ?: emptySet()
            preferences[FAVORITES_KEY] = currentFavorites + groupName
        }
    }

    suspend fun removeFavorite(groupName: String) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY] ?: emptySet()
            preferences[FAVORITES_KEY] = currentFavorites - groupName
        }
    }

    suspend fun toggleFavorite(groupName: String): Boolean {
        var isFavorite = false
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY] ?: emptySet()
            isFavorite = !currentFavorites.contains(groupName)
            if (isFavorite) {
                preferences[FAVORITES_KEY] = currentFavorites + groupName
            } else {
                preferences[FAVORITES_KEY] = currentFavorites - groupName
            }
        }
        return isFavorite
    }

    suspend fun isFavorite(groupName: String): Boolean {
        return favoritesFlow.map { favorites ->
            favorites.contains(groupName)
        }.first()
    }
}