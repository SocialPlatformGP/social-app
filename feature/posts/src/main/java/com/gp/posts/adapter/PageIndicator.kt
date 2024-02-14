package com.gp.posts.adapter

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.gp.socialapp.database.model.PostAttachment

//Todo make it more generic and reusable for other projects as well
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePagerZ(
    pageCount: Int,
    images: List<PostAttachment> = emptyList(),
    onImageClicked: (PostAttachment) -> Unit
) {
    val width = LocalConfiguration.current.screenWidthDp.dp
    Box(
        modifier = Modifier.size(width, 400.dp),
    ) {
        val pagerState = rememberPagerState(
            pageCount = { pageCount },
        )
        val indicatorScrollState = rememberLazyListState()

        LaunchedEffect(key1 = pagerState.currentPage, block = {
            val currentPage = pagerState.currentPage
            val size = indicatorScrollState.layoutInfo.visibleItemsInfo.size
            val lastVisibleIndex =
                indicatorScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val firstVisibleItemIndex = indicatorScrollState.firstVisibleItemIndex

            if (currentPage > lastVisibleIndex - 1) {
                indicatorScrollState.animateScrollToItem(currentPage - size + 2)
            } else if (currentPage <= firstVisibleItemIndex + 1) {
                indicatorScrollState.animateScrollToItem(Math.max(currentPage - 1, 0))
            }
        })
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onImageClicked(images[pagerState.currentPage])
                    }
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = images[pagerState.currentPage].url,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentScale = ContentScale.FillBounds,

                    ) {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                        CircularProgressIndicator()
                    } else {
                        SubcomposeAsyncImageContent()
                    }
                }

            }
        }
        LazyRow(
            state = indicatorScrollState,
            modifier = Modifier
                .offset(y = (-16).dp)
                .height(25.dp)
                .width(((6 + 16) * 2 + 3 * (10 + 16)).dp)
                .background(Color.LightGray.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .align(Alignment.BottomCenter)
            ,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                item(key = "item$iteration") {
                    val currentPage = pagerState.currentPage
                    val firstVisibleIndex by remember { derivedStateOf { indicatorScrollState.firstVisibleItemIndex } }
                    val lastVisibleIndex =
                        indicatorScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                            ?: 0
                    val size by animateDpAsState(
                        targetValue = if (iteration == currentPage) {
                            10.dp
                        } else if (iteration in firstVisibleIndex + 1..lastVisibleIndex - 1) {
                            10.dp
                        } else {
                            6.dp
                        }, label = ""
                    )
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(color, CircleShape)
                            .size(
                                size
                            )
                    )
                }
            }
        }

    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun fff() {
    ImagePagerZ(
        pageCount = 6,
        images = emptyList(),
        onImageClicked = {}
    )

}