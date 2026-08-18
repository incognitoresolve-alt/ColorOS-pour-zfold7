package com.coloroslauncher.core.model

import android.graphics.drawable.Drawable
import android.os.UserHandle

/** Immutable snapshot of an installed, launchable application. */
data class AppInfo(
    val packageName: String,
    val activityClassName: String,
    val label: String,
    val user: UserHandle,
    val icon: Drawable,
    val badgeCount: Int = 0,
) {
    /** Stable identity across the launcher-core & Room layers (a package can have several activities/profiles). */
    val componentKey: String
        get() = "$packageName/$activityClassName/${user.hashCode()}"
}
