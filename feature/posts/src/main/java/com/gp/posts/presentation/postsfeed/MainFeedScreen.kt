package com.gp.posts.presentation.postsfeed

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gp.socialapp.model.Post
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainFeedScreen(viewModel: FeedPostViewModel) {
    val state by viewModel.state.collectAsState()
    MainFeedScreen(state)
}

@Composable
fun MainFeedScreen(state: FeedPostUIState) {
    Scaffold { paddingValues ->
        MainFeedContent(
            paddingValues = paddingValues,
            posts = state.posts
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainFeedContent(
    paddingValues: PaddingValues,
    posts:List<Post>
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { tabItems.size }
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex
        ) {
            tabItems.forEachIndexed { index, tabItem ->
                Tab(
                    modifier = Modifier.padding(8.dp).height(52.dp),
                    selected = (index == selectedTabIndex),
                    onClick = {
                        selectedTabIndex = index
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(tabItem.title)
                           },
                    icon = {
                        Icon(imageVector = tabItem.imageVector, contentDescription = null)
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) { index ->
            when (index) {
                0 -> {
                    FeedPostScreen(posts = posts)
                }
                1 -> {
                    FeedPostScreen(posts = posts.filter { it.type == "vip" })
                }
            }
        }
    }
    LaunchedEffect(pagerState.currentPage,pagerState.isScrollInProgress){
        if(!pagerState.isScrollInProgress){
            selectedTabIndex = pagerState.currentPage
        }
    }

}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainFeedPreview() {
    MainFeedScreen(FeedPostUIState())
}

data class TabItem(val title: String,val imageVector: ImageVector)

val tabItems = listOf(
    TabItem("All", Icons.Filled.AllInclusive),
    TabItem("Vip", Icons.Filled.NotificationImportant),
    )