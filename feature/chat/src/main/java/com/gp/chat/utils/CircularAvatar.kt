package com.gp.chat.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers

@Composable
fun CircularAvatar(
    imageURL: String,
    size: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val imageRequest =
        ImageRequest.Builder(LocalContext.current).data(imageURL).dispatcher(Dispatchers.IO)
            .memoryCacheKey(imageURL).diskCacheKey(imageURL).diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED).build()
    AsyncImage(
        model = imageRequest,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable { onClick() },
    )
}