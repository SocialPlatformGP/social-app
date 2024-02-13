package com.gp.posts.presentation.postDetails

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gp.posts.presentation.feedUiEvents.PostEvent
import com.gp.posts.presentation.feedUiEvents.ReplyEvent
import com.gp.posts.presentation.postsfeed.FeedPostItem
import com.gp.socialapp.model.NestedReplyItem
import com.gp.socialapp.model.Post
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun PostDetailsScreen(
    viewModel: PostDetailsViewModel
) {
    val postState by viewModel.currentPost.collectAsState()
    val repliesState by viewModel.currentReplies.collectAsState()
    PostDetailsScreen(
        postState,
        repliesState,
        onPostEvent = {
            viewModel.handlePostEvent(it)
        },
        onReplyEvent = {
            viewModel.handleReplyEvent(it)
        },
    )
    Log.d("inTop", "PostDetailsScreen: $repliesState")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsScreen(
    postState: Post,
    repliesState: NestedReplyItem,
    onPostEvent: (PostEvent) -> Unit,
    onReplyEvent: (ReplyEvent) -> Unit,
) {
    Scaffold { paddingValues ->
        Timber.tag("inTop1").d("PostDetailsScreen: " + repliesState)
        PostDetailsContent(
            modifier = Modifier.padding(paddingValues),
            post = postState,
            replies = repliesState,
            onPostEvent = onPostEvent,
            onReplyEvent = onReplyEvent,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsContent(
    modifier: Modifier = Modifier,
    replies: NestedReplyItem,
    post: Post,
    onPostEvent: (PostEvent) -> Unit,
    onReplyEvent: (ReplyEvent) -> Unit,
) {
    Box(modifier) {
        val postBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val replyBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val scope = rememberCoroutineScope()

        LazyColumn(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxSize(),
        ) {
            item {
                FeedPostItem(
                    post = post,
                    postEvent = {
                        when (it) {
                            is PostEvent.OnCommentClicked -> {
                                scope.launch {
                                    if (postBottomSheetState.isVisible) {
                                        postBottomSheetState.hide()
                                    } else {
                                        postBottomSheetState.show()
                                    }
                                }
                            }

                            else -> {
                                onPostEvent(it)
                            }
                        }
                    },
                )
            }
            commentList(comments = listOf(replies), onReplyEvent = {
                when (it) {
                    is ReplyEvent.OnAddReply -> {
                        scope.launch {
                            if (replyBottomSheetState.isVisible) {
                                replyBottomSheetState.hide()
                            } else {
                                replyBottomSheetState.show()
                            }
                        }
                    }

                    else -> {
                        onReplyEvent(it)
                    }
                }
            })
        }

        ModalBottomSheetLayout(
            content = {
                var value by remember { mutableStateOf("") }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    androidx.compose.material.OutlinedTextField(value = value,
                        onValueChange = { value = it },
                        label = { Text(text = "Add your comment") },
                        keyboardActions = KeyboardActions(onDone = {
                            onPostEvent(PostEvent.onCommentAdded(value))
                            value = ""
                            scope.launch {
                                postBottomSheetState.hide()
                            }
                        }),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ))
                    IconButton(onClick = {
                        onPostEvent(PostEvent.onCommentAdded(value))
                        value = ""
                        scope.launch {
                            postBottomSheetState.hide()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Send, contentDescription = null
                        )
                    }
                }
            },
            sheetState = postBottomSheetState,
            sheetShape = RoundedCornerShape(16.dp),
            sheetElevation = 8.dp,
            sheetBackgroundColor = MaterialTheme.colors.surface,
            sheetContentColor = MaterialTheme.colors.onSurface,
            sheetGesturesEnabled = true,
            sheetContent = {}
        )
        ModalBottomSheetLayout(
            content = {
                var value by remember { mutableStateOf("") }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    androidx.compose.material.OutlinedTextField(value = value,
                        onValueChange = { value = it },
                        label = { Text(text = "Add your comment") },
                        keyboardActions = KeyboardActions(onDone = {
                            onReplyEvent(ReplyEvent.OnReplyAdded(value))
                            value = ""
                            scope.launch {
                                replyBottomSheetState.hide()
                            }
                        }),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ))
                    IconButton(onClick = {
                        onReplyEvent(ReplyEvent.OnReplyAdded(value))
                        value = ""
                        scope.launch {
                            replyBottomSheetState.hide()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Send, contentDescription = null
                        )
                    }
                }
            },
            sheetState = replyBottomSheetState,
            sheetShape = RoundedCornerShape(16.dp),
            sheetElevation = 8.dp,
            sheetBackgroundColor = MaterialTheme.colors.surface,
            sheetContentColor = MaterialTheme.colors.onSurface,
            sheetGesturesEnabled = true,
            sheetContent = {}
        )

    }
}


@Preview(apiLevel = 33, showSystemUi = true, showBackground = true)
@Composable
fun PreviewPostDetailsScreen() {
    PostDetailsScreen(
        postState = Post(
            id = "1",
            title = "Title",
            authorEmail = "Author",
            publishedAt = "2021-09-01T00:00:00Z",
            body = "Body",
        ),
        repliesState = sampleData,
        onPostEvent = {},
        onReplyEvent = {},
    )
}
