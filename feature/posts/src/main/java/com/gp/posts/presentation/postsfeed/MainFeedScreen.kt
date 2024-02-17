package com.gp.posts.presentation.postsfeed

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Signpost
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gp.posts.adapter.FilesBottomSheet
import com.gp.posts.presentation.feedUiEvents.NavigationActions
import com.gp.posts.presentation.feedUiEvents.PostEvent
import com.gp.posts.presentation.utils.CurrentUser
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.model.Post
import com.gp.socialapp.theme.AppTheme
import com.gp.socialapp.theme.DarkColorScheme
import com.gp.socialapp.theme.LightColorScheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainFeedScreen(
    viewModel: FeedPostViewModel,
    postEvent: (PostEvent) -> Unit,
    navigationActions: (NavigationActions) -> Unit

) {
    val state by viewModel.state.collectAsState()
    val currentEmail by remember {
        mutableStateOf(
            CurrentUser.getCurrentUser()?.email ?: ""
        )
    }
    var bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var currentAttachments by remember {
        mutableStateOf(
            listOf(
                PostAttachment(
                    name = "file1",
                    url = "https://www.google.com",
                    type = "image"
                ),
                PostAttachment(
                    name = "file2",
                    url = "https://www.google.com",
                    type = "image"
                ),
                PostAttachment(
                    name = "file3",
                    url = "https://www.google.com",
                    type = "image"
                ),
                PostAttachment(
                    name = "file4",
                    url = "https://www.google.com",
                    type = "image"
                ),
            )
        )
    }
    MainFeedScreen(
        state,
        postEvent = { action ->
            when (action) {
                is PostEvent.OnPostUpVoted -> {
                    viewModel.upVote(action.post)
                }

                is PostEvent.OnPostDownVoted -> {
                    viewModel.downVote(action.post)
                }

                is PostEvent.OnPostDeleted -> {
                    viewModel.deletePost(action.post)
                }

                is PostEvent.OnViewFilesAttachmentClicked -> {
                    Log.d("zarea159", "FeedPostScreen: " + action.attachments.size)
                    currentAttachments = action.attachments
                    scope.launch {
                        bottomSheetState.show()
                    }
                }

                else -> {
                    postEvent(action)
                }
            }
        },
        navigationActions = navigationActions,
        currentEmail = currentEmail

    )
    FilesBottomSheet(
        currentAttachments = currentAttachments,
        bottomSheetState = bottomSheetState,
        postEvent = postEvent
    )
}


@Composable
fun MainFeedScreen(
    state: FeedPostUIState,
    postEvent: (PostEvent) -> Unit,
    currentEmail: String,
    navigationActions: (NavigationActions) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            Fab {
                postEvent(PostEvent.OnAddPost)
            }
        },
        topBar = {
            MainFeedTopBar(navigationAction = navigationActions)
        },
        bottomBar = {
            MainFeedBottomBar(navigationAction = navigationActions)
        },
        backgroundColor = LightColorScheme.surfaceVariant
    ) { paddingValues ->
        MainFeedContent(
            paddingValues = paddingValues,
            posts = state.posts,
            postEvent = postEvent,
            currentEmail = currentEmail


        )
    }
}

@Composable
fun MainFeedBottomBar(navigationAction: (NavigationActions) -> Unit) {
    BottomNavigation(
        backgroundColor = if(isSystemInDarkTheme()) DarkColorScheme.onPrimaryContainer else LightColorScheme.onPrimaryContainer,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navigationAction(NavigationActions.NavigateToChat)
            }) {
                Icon(
                    imageVector = Icons.Filled.ChatBubble,
                    contentDescription = "chat",
                        tint = if(isSystemInDarkTheme()) DarkColorScheme.onPrimary else LightColorScheme.onPrimary
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Folder,
                    contentDescription = "my files",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Signpost,
                    contentDescription = "post",
                        tint = if(isSystemInDarkTheme()) DarkColorScheme.onPrimary else LightColorScheme.onPrimary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFeedTopBar(navigationAction: (NavigationActions) -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "EduLink",
                color = if(isSystemInDarkTheme()) DarkColorScheme.onPrimary else LightColorScheme.onPrimary,
                style = MaterialTheme.typography.h6
            )
        },
        actions = {
            IconButton(onClick = {
                navigationAction(NavigationActions.NavigateToSearch)
            }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "search",
                    tint = if(isSystemInDarkTheme()) DarkColorScheme.onPrimary else LightColorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = if(isSystemInDarkTheme()) DarkColorScheme.onPrimaryContainer else LightColorScheme.onPrimaryContainer,
        )
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainFeedContent(
    paddingValues: PaddingValues,
    posts: List<Post>,
    postEvent: (PostEvent) -> Unit,
    currentEmail: String
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { tabItems.size }
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
//            .background(color = Color.LightGray.copy(alpha = 0.5f))
    ) {
        androidx.compose.material3.TabRow(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(),
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions ->
                androidx.compose.material3.TabRowDefaults.Indicator(
                    color = if(isSystemInDarkTheme()) DarkColorScheme.onPrimary else LightColorScheme.onPrimary,
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .padding(horizontal = 50.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally),
                    height = 4.dp
                )
            },
            containerColor = if(isSystemInDarkTheme()) DarkColorScheme.onPrimaryContainer else LightColorScheme.onPrimaryContainer,
        ) {
            tabItems.forEachIndexed { index, tabItem ->
                Tab(
                    selected = (index == selectedTabIndex),
                    onClick = {
                        selectedTabIndex = index
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = tabItem.title,
                            color = if(isSystemInDarkTheme()) DarkColorScheme.onPrimary else LightColorScheme.onPrimary,
                            fontSize = 16.sp
                        )
                    },

                    )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) { index ->
            when (index) {
                0 -> {
                    FeedPostScreen(
                        posts = posts,
                        postEvent = postEvent,
                        currentEmail = currentEmail
                    )
                }

                1 -> {
                    FeedPostScreen(
                        posts = posts.filter { it.type == "vip" },
                        postEvent = postEvent,
                        currentEmail = currentEmail
                    )
                }
            }
        }
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }

}

@Composable
fun Fab(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = if(isSystemInDarkTheme()) DarkColorScheme.onPrimaryContainer else LightColorScheme.onPrimaryContainer,
        contentColor = if(isSystemInDarkTheme()) DarkColorScheme.onPrimary else LightColorScheme.onPrimary
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null
        )
    }
}


@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun MainFeedPreview_Night() {
    AppTheme {
        Surface {
            MainFeedScreen(
                FeedPostUIState(
                    posts = listOf(
                        Post(
                            userName = "John Doe5",
                            authorEmail = "d",
                            publishedAt = "2021-09-01T00:00:00Z",
                            type = "vip",
                            title = "Title",
                            body = "Body",
                        ),
                        Post(
                            userName = "John Doe",
                            authorEmail = "d",
                            publishedAt = "2021-09-01T00:00:00Z",
                            type = "all",
                            title = "Title",
                            body = "Body",
                        ),

                        ),
                ),
                postEvent = {},
                currentEmail = "d",
                navigationActions = {}
            )
        }
    }

}
@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun MainFeedPreview_Light() {
    AppTheme {
        Surface {
            MainFeedScreen(
                FeedPostUIState(
                    posts = listOf(
                        Post(
                            userName = "John Doe5",
                            authorEmail = "d",
                            publishedAt = "2021-09-01T00:00:00Z",
                            type = "vip",
                            title = "Title",
                            body = "Body",
                        ),
                        Post(
                            userName = "John Doe",
                            authorEmail = "d",
                            publishedAt = "2021-09-01T00:00:00Z",
                            type = "all",
                            title = "Title",
                            body = "Body",
                        ),

                        ),
                ),
                postEvent = {},
                currentEmail = "d",
                navigationActions = {}
            )
        }
    }

}
data class TabItem(val title: String, val imageVector: ImageVector)

val tabItems = listOf(
    TabItem("General", Icons.Filled.AllInclusive),
    TabItem("Spotlight", Icons.Filled.NotificationImportant),
)