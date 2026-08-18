package com.coloroslauncher.widgets

import android.appwidget.AppWidgetProviderInfo
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/** Embeds a live [AppWidgetHostView] (e.g. a clock/weather widget) inside Compose content. */
@Composable
fun WidgetHostContainer(
    widgetHostManager: WidgetHostManager,
    appWidgetId: Int,
    providerInfo: AppWidgetProviderInfo,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { widgetHostManager.createHostView(appWidgetId, providerInfo) },
    )
}
