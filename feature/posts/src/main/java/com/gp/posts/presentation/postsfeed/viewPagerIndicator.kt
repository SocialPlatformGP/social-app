package com.gp.posts.presentation.postsfeed

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager

@Composable
fun ViewPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    indicatorColor: Color,
    indicatorSize: Dp = 8.dp,
    indicatorSpacing: Dp = 8.dp
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentWidth(align = Alignment.CenterHorizontally),
        horizontalArrangement = Arrangement.spacedBy(indicatorSpacing)
    ) {
        repeat(pageCount) { pageIndex ->
            PageIndicator(
                isSelected = pageIndex == currentPage,
                indicatorColor = indicatorColor,
                indicatorSize = indicatorSize
            )
        }
    }
}

@Composable
fun PageIndicator(
    isSelected: Boolean,
    indicatorColor: Color,
    indicatorSize: Dp
) {
    val contentColor = contentColorFor(indicatorColor)

    Box(
        modifier = Modifier
            .size(indicatorSize)
            .clip(CircleShape)
            .background(
                color = if (isSelected) indicatorColor else Color.Gray,
                shape = CircleShape
            )
            .padding(4.dp) // Adjust as needed
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = contentColor,
                    shape = CircleShape
                )
        )
    }
}