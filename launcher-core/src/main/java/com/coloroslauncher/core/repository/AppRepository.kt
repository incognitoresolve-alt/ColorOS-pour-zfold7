package com.coloroslauncher.core.repository

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.Rect
import android.os.UserHandle
import com.coloroslauncher.core.model.AppInfo
import com.coloroslauncher.core.model.LaunchSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Wraps [LauncherApps] to list installed launchable activities and to observe live
 * install/uninstall/update events, across all user profiles (work profile support included).
 */
class AppRepository(context: Context) {

    private val appContext = context.applicationContext
    private val launcherApps =
        appContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    fun loadInstalledApps(): List<AppInfo> {
        val userManager = appContext.getSystemService(Context.USER_SERVICE) as android.os.UserManager
        return userManager.userProfiles.flatMap { user -> loadForUser(user) }
    }

    private fun loadForUser(user: UserHandle): List<AppInfo> {
        val iconSizePx = (appContext.resources.displayMetrics.density * ICON_SIZE_DP).toInt()
        return launcherApps.getActivityList(null, user).map { activity ->
            AppInfo(
                packageName = activity.applicationInfo.packageName,
                activityClassName = activity.componentName.className,
                label = activity.label.toString(),
                user = user,
                icon = activity.getIcon(iconSizePx) ?: activity.getBadgedIcon(0),
            )
        }
    }

    /** Launches [app], zooming out from [source]'s icon bounds when available (ColorOS-style open transition). */
    fun launch(app: AppInfo, source: LaunchSource? = null) {
        val options = source?.let {
            ActivityOptions.makeScaleUpAnimation(it.view, it.left, it.top, it.width, it.height).toBundle()
        }
        val bounds = source?.let { Rect(it.left, it.top, it.left + it.width, it.top + it.height) }
        launcherApps.startMainActivity(
            ComponentName(app.packageName, app.activityClassName),
            app.user,
            bounds,
            options,
        )
    }

    /** Emits the full app list again whenever any package is added, removed, or changed. */
    fun observeAppListChanges(): Flow<Unit> = callbackFlow {
        val callback = object : LauncherApps.Callback() {
            override fun onPackageRemoved(packageName: String, user: UserHandle) { trySend(Unit) }
            override fun onPackageAdded(packageName: String, user: UserHandle) { trySend(Unit) }
            override fun onPackageChanged(packageName: String, user: UserHandle) { trySend(Unit) }
            override fun onPackagesAvailable(
                packageNames: Array<out String>,
                user: UserHandle,
                replacing: Boolean,
            ) { trySend(Unit) }
            override fun onPackagesUnavailable(
                packageNames: Array<out String>,
                user: UserHandle,
                replacing: Boolean,
            ) { trySend(Unit) }
        }
        launcherApps.registerCallback(callback)
        awaitClose { launcherApps.unregisterCallback(callback) }
    }

    companion object {
        private const val ICON_SIZE_DP = 48
    }
}
