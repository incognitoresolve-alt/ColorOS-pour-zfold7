package com.coloroslauncher.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context

/**
 * Thin wrapper around the native [AppWidgetHost] / [AppWidgetManager] APIs so third-party
 * widgets (weather, clock, calendar...) can be embedded on the home screen and, later, in the
 * "Smart Assistant" panel.
 *
 * [startListening]/[stopListening] must be called from the hosting Activity's onStart/onStop so
 * widgets keep receiving live updates only while the launcher is visible.
 */
class WidgetHostManager(context: Context) {

    private val appContext = context.applicationContext
    private val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(appContext)
    private val appWidgetHost = AppWidgetHost(appContext, HOST_ID)

    fun startListening() = appWidgetHost.startListening()

    fun stopListening() = appWidgetHost.stopListening()

    fun allocateAppWidgetId(): Int = appWidgetHost.allocateAppWidgetId()

    fun deleteAppWidgetId(appWidgetId: Int) = appWidgetHost.deleteAppWidgetId(appWidgetId)

    fun installedProviders(): List<AppWidgetProviderInfo> =
        appWidgetManager.installedProviders

    /** True if the widget provider still needs the user's explicit bind permission grant. */
    fun bindAppWidgetIfAllowed(appWidgetId: Int, provider: AppWidgetProviderInfo): Boolean =
        appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, provider.provider)

    fun createHostView(appWidgetId: Int, providerInfo: AppWidgetProviderInfo): AppWidgetHostView =
        appWidgetHost.createView(appContext, appWidgetId, providerInfo)

    fun getAppWidgetInfo(appWidgetId: Int): AppWidgetProviderInfo? =
        appWidgetManager.getAppWidgetInfo(appWidgetId)

    companion object {
        /** Arbitrary but stable id identifying this launcher as a widget host, must be unique per app. */
        private const val HOST_ID = 1042
    }
}
