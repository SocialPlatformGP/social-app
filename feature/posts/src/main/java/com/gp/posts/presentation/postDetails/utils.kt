package com.gp.posts.presentation.postDetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.unit.IntSize
import com.gp.socialapp.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
@Composable
fun SetTimeTillNow(time: String?) {
    var text by remember { mutableStateOf(DateUtils.calculateTimeDifference(time!!)) }

    LaunchedEffect(time) {
        val job = launch(Dispatchers.Default) {
            repeat(60) {
                delay(60000)
                withContext(Dispatchers.Main) {
                    text = DateUtils.calculateTimeDifference(time!!)
                }
            }
        }
    }

}

