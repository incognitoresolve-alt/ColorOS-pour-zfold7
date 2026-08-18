package com.coloroslauncher.widgets

import android.appwidget.AppWidgetProviderInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/** Minimal in-app widget picker, listing every installed [AppWidgetProviderInfo] to add to the home screen. */
@Composable
fun WidgetPicker(
    providers: List<AppWidgetProviderInfo>,
    onSelect: (AppWidgetProviderInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        items(providers, key = { it.provider.flattenToString() }) { provider ->
            val packageManager = LocalContext.current.packageManager
            Text(
                text = provider.loadLabel(packageManager),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(provider) }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
