package com.gp.posts.presentation.postsfeed

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedScreen(
               viewModel: FeedPostViewModel,
               feedToCreate:()->Unit,
               details: (Post) -> Unit,
               edit: (Post) -> Unit,
               onTagClicked: (Tag)->Unit) {

    val uiState by viewModel.uiState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        FeedContent(
            state = uiState,
            deletePost = { viewModel.deletePost(it) },
            details = details,
            edit = edit,
            onUpVote = { viewModel.upVote(it) },
            onDownVote = { viewModel.downVote(it) },
            onTagClicked = { onTagClicked(it) })
        androidx.compose.material.FloatingActionButton(
            onClick = {
                feedToCreate()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
    }

}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedContent(
    state: FeedPostUiState,
    deletePost: (Post) -> Unit,
    details: (Post) -> Unit,
    edit: (Post) -> Unit,
    onUpVote: (Post) -> Unit,
    onDownVote: (Post) -> Unit,
    onTagClicked: (Tag) -> Unit
) {
    LazyColumn {
        items(state.posts) {post->
            VipPostItem(
                deletePost=deletePost,
                details = details,
                edit = edit,
                onUpVote=onUpVote,
                onDownVote=onDownVote,
                onTagClicked=onTagClicked,
                post = post )
        }
    }
}
