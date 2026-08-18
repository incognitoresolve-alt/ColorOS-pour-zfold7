package com.coloroslauncher.core.repository

import android.app.WallpaperManager
import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.coloroslauncher.core.model.FoldState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.wallpaperDataStore by preferencesDataStore(name = "wallpaper_prefs")

/**
 * Stores the user's chosen wallpaper per fold posture (a distinct image can be set for the
 * cover screen vs. the unfolded inner screen). Android exposes a single system wallpaper slot,
 * so the matching image is (re)applied via [WallpaperManager] whenever [applyForFoldState] is
 * called with the posture the device just transitioned into.
 */
class WallpaperRepository(private val context: Context) {

    private val wallpaperManager = WallpaperManager.getInstance(context)

    fun observeCoverWallpaperUri(): Flow<Uri?> = observeUri(KEY_COVER)
    fun observeUnfoldedWallpaperUri(): Flow<Uri?> = observeUri(KEY_UNFOLDED)

    private fun observeUri(key: Preferences.Key<String>): Flow<Uri?> =
        context.wallpaperDataStore.data.map { prefs -> prefs[key]?.let(Uri::parse) }

    /** Persists [uri] as the wallpaper for [foldState]'s slot and applies it right away. */
    suspend fun setWallpaper(foldState: FoldState, uri: Uri) {
        context.wallpaperDataStore.edit { it[keyFor(foldState)] = uri.toString() }
        applyUri(uri)
    }

    /** Re-applies whichever wallpaper is stored for [foldState], e.g. after a fold/unfold event. */
    suspend fun applyForFoldState(foldState: FoldState) {
        val uriString = context.wallpaperDataStore.data.first()[keyFor(foldState)] ?: return
        applyUri(Uri.parse(uriString))
    }

    private fun applyUri(uri: Uri) {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            wallpaperManager.setStream(stream)
        }
    }

    private fun keyFor(foldState: FoldState) = when (foldState) {
        FoldState.UNFOLDED_FLAT, FoldState.FLEX -> KEY_UNFOLDED
        FoldState.COVER_OR_UNKNOWN -> KEY_COVER
    }

    companion object {
        private val KEY_COVER = stringPreferencesKey("wallpaper_cover_uri")
        private val KEY_UNFOLDED = stringPreferencesKey("wallpaper_unfolded_uri")
    }
}
