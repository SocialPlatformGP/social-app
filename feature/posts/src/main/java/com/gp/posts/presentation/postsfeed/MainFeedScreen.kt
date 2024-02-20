package com.gp.posts.presentation.postsfeed

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
                is PostEvent.OnCommentClicked -> {
                    navigationActions(NavigationActions.NavigateToPostDetails(action.post))
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
        backgroundColor =androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer
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
        backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(
                RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            buttonNavigationItems.forEach { item ->
                ButtonNavItem(
                    when(item.title){
                        "Home" -> {
                            {
                                navigationAction(NavigationActions.NavigateToPost)
                            }
                        }
                        "Chat" -> {
                            {
                                navigationAction(NavigationActions.NavigateToChat)
                            }
                        }
                        "Material" -> {
                            {
                                navigationAction(NavigationActions.NavigateToMaterial)
                            }
                        }
                        else -> { {} }
                    },
                    item.title,
                    item.imageVector
                )
            }

        }
    }
}

@Composable
private fun ButtonNavItem(
    navigationAction: () -> Unit,
    title: String,
    imageVector: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = navigationAction,
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = "chat",
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
            )
        }
        Text(
            text = title,
            fontSize = 10.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
        )
    }
}


@Composable
fun MainFeedTopBar(navigationAction: (NavigationActions) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
            )
            .padding(16.dp)

    ) {
        IconButton(onClick = {
            navigationAction(NavigationActions.NavigateToSearch)
        }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "search",
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = "EduLink",
            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(align = Alignment.CenterHorizontally)
        )
        IconButton(onClick = {
            navigationAction(NavigationActions.NavigateToNotification)
        }) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = "notification",
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
            )
        }

    }
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
    ) {
        TabRow(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth().clip(RoundedCornerShape(
                    bottomStart = 8.dp,
                    bottomEnd = 8.dp
                )),
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions ->
                androidx.compose.material3.TabRowDefaults.Indicator(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .padding(horizontal = 50.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally),
                    height = 8.dp
                )
            },
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
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
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
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
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
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
val buttonNavigationItems = listOf(
    TabItem("Home", Icons.Filled.Home),
    TabItem("Chat", Icons.Filled.Chat),
    TabItem("Material", Icons.Filled.Folder),
)


