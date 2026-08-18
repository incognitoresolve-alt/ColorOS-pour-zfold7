package com.coloroslauncher.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coloroslauncher.core.model.AppInfo
import com.coloroslauncher.theme.ThemeConfig

/** Single ColorOS-style squircle app icon with label and optional notification badge. */
@Composable
fun AppIconView(
    app: AppInfo,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Image(
                bitmap = app.icon.toBitmap().asImageBitmap(),
                contentDescription = app.label,
                modifier = Modifier
                    .size(56.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(ThemeConfig.ICON_CORNER_RADIUS)),
            )
            if (app.badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (app.badgeCount > 99) "99+" else app.badgeCount.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                    )
                }
            }
        }
        if (showLabel) {
            Text(
                text = app.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

private fun android.graphics.drawable.Drawable.toBitmap(): android.graphics.Bitmap {
    if (this is android.graphics.drawable.BitmapDrawable) return bitmap
    val width = intrinsicWidth.coerceAtLeast(1)
    val height = intrinsicHeight.coerceAtLeast(1)
    val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}
