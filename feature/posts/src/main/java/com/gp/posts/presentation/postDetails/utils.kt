package com.gp.posts.presentation.postDetails
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.PagerState

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp


import kotlin.math.absoluteValue
import kotlin.math.sin
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import com.gp.posts.R
import kotlinx.coroutines.delay

import kotlin.math.PI




@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    minimizedMaxLines: Int = 1,
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
        )
        if (!expanded && seeMoreOffset != null) {
            val density = LocalDensity.current
            Text(
                "... See more",
                onTextLayout = { seeMoreSizeState.value = it.size },
                modifier = Modifier
                    .offset(
                        x = with(density) { seeMoreOffset.x.toDp() },
                        y = with(density) { seeMoreOffset.y.toDp() },
                    )
                    .clickable {
                        expanded = true
                        cutText = null
                    }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.bounceDotTransition(
    pagerState: PagerState,
    jumpOffset: Float,
    jumpScale: Float
): Modifier = graphicsLayer {
    val targetScale = jumpScale - 1f
    val distance = size.width + 8.dp.toPx()
    val pageOffset = pagerState.currentPageOffsetFraction
    val scrollPosition = pagerState.currentPage + pageOffset
    val current = scrollPosition.toInt()
    val settledPage = pagerState.settledPage

    translationX = scrollPosition * distance

    val scale = if (pageOffset.absoluteValue < 0.5) {
        1.0f + (pageOffset.absoluteValue * 2) * targetScale
    } else {
        jumpScale + ((1 - (pageOffset.absoluteValue * 2)) * targetScale)
    }

    scaleX = scale
    scaleY = scale

    val factor = (pageOffset.absoluteValue * PI).toFloat()
    val y = if (current >= settledPage) -sin(factor) * jumpOffset else sin(factor) * distance / 2
    translationY += y
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BouncingImagePagerYes(images: List<Int>) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        images.size
    }

    LazyRow(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(images) { imageResId ->
            val graphicsLayerModifier = Modifier.graphicsLayer {
                val jumpOffset = 20.dp.toPx()
                val jumpScale = 1.2f

                val targetScale = jumpScale - 1f
                val distance = size.width + 8.dp.toPx()
                val pageOffset = pagerState.currentPageOffsetFraction
                val scrollPosition = pagerState.currentPage + pageOffset
                val current = scrollPosition.toInt()
                val settledPage = pagerState.settledPage

                translationX = scrollPosition * distance

                val scale = if (pageOffset.absoluteValue < 0.5) {
                    1.0f + (pageOffset.absoluteValue * 2) * targetScale
                } else {
                    jumpScale + ((1 - (pageOffset.absoluteValue * 2)) * targetScale)
                }

                scaleX = scale
                scaleY = scale

                val factor = (pageOffset.absoluteValue * PI).toFloat()
                val y =
                    if (current >= settledPage) -sin(factor) * jumpOffset else sin(factor) * distance / 2
                translationY += y
            }

            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .then(graphicsLayerModifier)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DotIndicator(scrollState: PagerState, modifier: Modifier = Modifier) {
    // Implement your DotIndicator logic here
    // You can use AndroidViewBinding or compose your DotIndicator manually
    // ...

    // For simplicity, I'm just printing the current page index
    Text(
        text = "Page ${scrollState.initialPage}",
        modifier = modifier
    )
}



@Composable
fun ImageSlideshow(images: List<Int>) {
    var currentPage by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentPage) {
        while (true) {
            delay(2000) // Change the delay duration as needed (e.g., 2000 milliseconds or 2 seconds)
            currentPage = (currentPage + 1) % images.size
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(images) { imageResId ->
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .graphicsLayer {

                            val jumpOffset = 20.dp.toPx()
                            val jumpScale = 1.2f

                            val targetScale = jumpScale - 1f
                            val distance = size.width + 8.dp.toPx()
                            val pageOffset = 0f // Assume no scrolling
                            val scrollPosition = 0f
                            val current = 0
                            val settledPage = 0

                            translationX = scrollPosition * distance

                            val scale = if (pageOffset.absoluteValue < 0.5) {
                                1.0f + (pageOffset.absoluteValue * 2) * targetScale
                            } else {
                                jumpScale + ((1 - (pageOffset.absoluteValue * 2)) * targetScale)
                            }

                            scaleX = scale
                            scaleY = scale

                            val factor = (pageOffset.absoluteValue * Math.PI)
                            val y =
                                if (current >= settledPage) -sin(factor) * jumpOffset else sin(factor) * distance / 2
                            translationY += y.toFloat()

                    }
            )
        }
    }
}

@Preview(showBackground = true, apiLevel = 30)
@Composable
fun ImageSlideshowPreview() {
    val imageList = listOf(
        R.drawable.pngwing_com,
        R.drawable.pngwing_com,
        R.drawable.women
    )
   // ImageSlideshow(images = imageList)
}
