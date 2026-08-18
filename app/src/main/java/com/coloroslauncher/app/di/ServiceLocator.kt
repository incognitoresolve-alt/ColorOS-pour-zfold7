package com.coloroslauncher.app.di

import android.content.Context
import com.coloroslauncher.core.db.LauncherDatabase
import com.coloroslauncher.core.repository.AppRepository
import com.coloroslauncher.core.repository.LayoutRepository
import com.coloroslauncher.core.repository.SettingsRepository
import com.coloroslauncher.core.repository.WallpaperRepository
import com.coloroslauncher.widgets.WidgetHostManager

/**
 * Minimal hand-rolled dependency container (no Hilt/Dagger) to keep the launcher lightweight,
 * per the project's "no non-essential framework dependency" constraint. Instances are created
 * lazily and shared for the app process's lifetime, mirroring what a DI graph would provide.
 */
class ServiceLocator(context: Context) {

    private val appContext = context.applicationContext

    val appRepository: AppRepository by lazy { AppRepository(appContext) }

    val layoutRepository: LayoutRepository by lazy {
        val db = LauncherDatabase.getInstance(appContext)
        LayoutRepository(db.gridItemDao(), db.folderDao())
    }

    val wallpaperRepository: WallpaperRepository by lazy { WallpaperRepository(appContext) }

    val settingsRepository: SettingsRepository by lazy { SettingsRepository(appContext) }

    val widgetHostManager: WidgetHostManager by lazy { WidgetHostManager(appContext) }
}
