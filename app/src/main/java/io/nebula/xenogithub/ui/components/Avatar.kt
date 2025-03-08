package io.nebula.xenogithub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.invisibleToUser
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation

/**
 * Created by nebula on 2025/3/6
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Avatar(
    url: String?,
    modifier: Modifier = Modifier,
) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(url).memoryCacheKey(url).apply {
            transformations(CircleCropTransformation())
        }.build()
    )
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
            .clearAndSetSemantics { invisibleToUser() }
            .clip(CircleShape),
    )
}