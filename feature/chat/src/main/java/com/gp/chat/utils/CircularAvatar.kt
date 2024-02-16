package com.gp.chat.utils

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.gp.chat.R
import com.gp.chat.presentation.theme.AppTheme
import kotlinx.coroutines.Dispatchers

@Composable
fun CircularAvatar(
    imageURL: String,
    size: Dp,
    modifier: Modifier = Modifier,
    @DrawableRes placeHolderDrawable: Int = R.drawable.baseline_account_circle_24,
    onClick: () -> Unit = {},
) {
    val avatarModifier = modifier
        .size(size)
        .clip(CircleShape)
        .clickable { onClick() }
    if (imageURL.isNotBlank()) {
        val imageRequest =
            ImageRequest.Builder(LocalContext.current).data(imageURL).dispatcher(Dispatchers.IO)
                .memoryCacheKey(imageURL).diskCacheKey(imageURL)
                .diskCachePolicy(CachePolicy.ENABLED).memoryCachePolicy(CachePolicy.ENABLED).build()
        AsyncImage(
            model = imageRequest,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = avatarModifier
        )
    } else {
        Icon(
            painter = painterResource(id = placeHolderDrawable),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = avatarModifier
        )
    }
}

@Composable
fun CircularAvatar(
    imageURL: String,
    size: Dp,
    modifier: Modifier = Modifier,
    placeHolderImageVector: ImageVector,
    onClick: () -> Unit = {},
) {
    val avatarModifier = modifier
        .size(size)
        .clip(CircleShape)
        .clickable { onClick() }
    if (imageURL.isNotBlank()) {
        val imageRequest =
            ImageRequest.Builder(LocalContext.current).data(imageURL).dispatcher(Dispatchers.IO)
                .memoryCacheKey(imageURL).diskCacheKey(imageURL)
                .diskCachePolicy(CachePolicy.ENABLED).memoryCachePolicy(CachePolicy.ENABLED).build()
        AsyncImage(
            model = imageRequest,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = avatarModifier
        )
    } else {
        Icon(
            imageVector = placeHolderImageVector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = avatarModifier
        )
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CircularAvatarPreview() {
    AppTheme {
        CircularAvatar(
            imageURL = "", size = 50.dp, placeHolderDrawable = R.drawable.ic_group
        )
    }
}