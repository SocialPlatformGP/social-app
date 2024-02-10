package com.gp.posts.presentation.postDetails

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gp.posts.presentation.feedUiEvents.PostEvent
import com.gp.posts.presentation.feedUiEvents.ReplyEvent
import com.gp.posts.presentation.postsfeed.FeedPostItem
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.model.NestedReplyItem
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag

@Composable
fun PostDetailsScreen(
    viewModel: PostDetailsViewModel
) {
    val postState by viewModel.currentPost.collectAsState()
    val repliesState by viewModel.currentReplies.collectAsState()
    PostDetailsScreen(
        postState,
        repliesState,
        onPostEvent = { viewModel.handlePostEvent(it) },
        onReplyEvent = { viewModel.handleReplyEvent(it) },
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
        Log.d("inTop1", "PostDetailsScreen: $repliesState")
        PostDetailsContent(
            modifier = Modifier.padding(paddingValues),
            post=postState,
            replies = repliesState,
            onPostEvent = onPostEvent,
            onReplyEvent = onReplyEvent,
        )
    }
}
@Composable
fun PostDetailsContent(
    modifier: Modifier = Modifier,
    replies: NestedReplyItem,
    post: Post,
    onPostEvent: (PostEvent) -> Unit,
    onReplyEvent: (ReplyEvent) -> Unit,
    ) {
    Box (modifier){
        LazyColumn(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxSize(),
        ) {
            item {
                FeedPostItem(
                    post =post ,
                    postEvent = onPostEvent,
                )
            }
            commentList(comments = listOf(replies),onReplyEvent=onReplyEvent)
        }
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
