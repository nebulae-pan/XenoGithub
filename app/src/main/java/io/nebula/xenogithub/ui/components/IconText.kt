package io.nebula.xenogithub.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * Created by nebula on 2025/3/7
 */
@Composable
fun IconText(@DrawableRes resId: Int, modifier: Modifier = Modifier, text: @Composable () -> Unit) {
    IconText(painter = painterResource(resId), modifier, text)
}

@Composable
fun IconText(painter: Painter, modifier: Modifier = Modifier, text: @Composable () -> Unit) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(painter, contentDescription = null, Modifier.size(12.dp))
        Spacer(Modifier.width(1.dp))
        text()
    }
}