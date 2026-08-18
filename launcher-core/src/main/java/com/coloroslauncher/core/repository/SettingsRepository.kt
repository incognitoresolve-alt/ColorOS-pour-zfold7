package com.coloroslauncher.core.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.coloroslauncher.theme.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "launcher_settings")

/** User-facing preferences: theme mode override and the set of temporarily hidden ("Focus mode") apps. */
class SettingsRepository(private val context: Context) {

    fun observeThemeMode(): Flow<AppThemeMode> =
        context.settingsDataStore.data.map { prefs ->
            prefs[KEY_THEME_MODE]?.let { runCatching { AppThemeMode.valueOf(it) }.getOrNull() }
                ?: AppThemeMode.SYSTEM
        }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.settingsDataStore.edit { it[KEY_THEME_MODE] = mode.name }
    }

    fun observeHiddenPackages(): Flow<Set<String>> =
        context.settingsDataStore.data.map { prefs ->
            prefs[KEY_HIDDEN_PACKAGES]?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
        }

    suspend fun setHiddenPackages(packages: Set<String>) {
        context.settingsDataStore.edit { it[KEY_HIDDEN_PACKAGES] = packages.joinToString(",") }
    }

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_HIDDEN_PACKAGES = stringPreferencesKey("hidden_packages")
    }
}
