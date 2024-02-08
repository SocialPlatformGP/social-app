package com.gp.posts.presentation.postDetails
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toFile
import coil.compose.rememberImagePainter
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.gp.posts.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

import kotlin.math.PI
import kotlin.math.sqrt
@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    minimizedMaxLines: Int = 2
) {
    var cutText by remember(text) { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    val seeMoreSizeState = remember { mutableStateOf<IntSize?>(null) }
    val seeMoreOffsetState = remember { mutableStateOf<Offset?>(null) }

    val textLayoutResult = textLayoutResultState.value
    val seeMoreSize = seeMoreSizeState.value
    val seeMoreOffset = seeMoreOffsetState.value

    LaunchedEffect(text, expanded, textLayoutResult, seeMoreSize) {
        val lastLineIndex = minimizedMaxLines - 1
        if (!expanded && textLayoutResult != null && seeMoreSize != null
            && lastLineIndex + 1 == textLayoutResult.lineCount
            && textLayoutResult.isLineEllipsized(lastLineIndex)
        ) {
            var lastCharIndex = textLayoutResult.getLineEnd(lastLineIndex, visibleEnd = true)
            seeMoreOffsetState.value = Offset(
                x = textLayoutResult.getCursorRect(lastCharIndex).left,
                y = textLayoutResult.getLineBottom(lastLineIndex)
            )
            cutText = text.substring(startIndex = 0, endIndex = lastCharIndex)
        }
    }

    Box(modifier) {
        Text(
            text = cutText ?: text,
            maxLines = if (expanded) Int.MAX_VALUE else minimizedMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResultState.value = it },
            modifier = Modifier.clickable { expanded = !expanded },
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 20.sp
            )
        )
        if (!expanded && seeMoreOffset != null) {
            val density = LocalDensity.current
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand",
                modifier = Modifier
                    .offset(
                        x = with(density) { seeMoreOffset.x.toDp() },
                        y = with(density) { seeMoreOffset.y.toDp() },
                    )
            )
        }
    }
}
