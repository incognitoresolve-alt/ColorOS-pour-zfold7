package com.coloroslauncher.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalView
import com.coloroslauncher.core.model.LaunchSource

/**
 * Tracks an icon's on-screen position so a tap can be turned into a [LaunchSource] anchoring a
 * zoom+fade app-open transition. Apply the returned [Modifier] to the tappable icon, then call
 * the returned lambda from the tap handler to get the current bounds (null before first layout).
 */
@Composable
fun rememberLaunchSourceCapture(): Pair<Modifier, () -> LaunchSource?> {
    val view = LocalView.current
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val modifier = Modifier.onGloballyPositioned { coordinates = it }
    val provider: () -> LaunchSource? = provider@{
        val current = coordinates ?: return@provider null
        val position = current.positionInWindow()
        LaunchSource(
            view = view,
            left = position.x.toInt(),
            top = position.y.toInt(),
            width = current.size.width,
            height = current.size.height,
        )
    }
    return modifier to provider
}
