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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.posts.R
import com.gp.posts.listeners.OnTagClicked
import com.gp.posts.presentation.postDetails.ExpandableText
import com.gp.posts.presentation.postDetails.UserPostTags
import com.gp.posts.presentation.postDetails.imageCaching
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag
import com.gp.socialapp.util.DateUtils
import com.gp.users.model.NetworkUser


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedScreen(
               viewModel: FeedPostViewModel,
               feedToCreate:()->Unit,
               details: (Post) -> Unit,
               edit: (Post) -> Unit,
               onTagClicked: (Tag)->Unit) {

    val uiState by viewModel.uiState.collectAsState()
    FeedContent(
        state = uiState,
        deletePost = {viewModel.deletePost(it)},
        details = details,
        edit = edit,
        onUpVote = {viewModel.upVote(it)},
        onDownVote ={viewModel.downVote(it)} ,
        onTagClicked = {onTagClicked(it)} )

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
            PostItem(
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

@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostItem(
    deletePost: (Post) -> Unit,
    details: (Post) -> Unit,
    edit: (Post) -> Unit,
    onUpVote: (Post) -> Unit,
    onDownVote: (Post) -> Unit,
    onTagClicked: (Tag) -> Unit,
    post: Post) {
    var isUpvoteFilled by remember { mutableStateOf(false) }
    var isDownvoteFilled by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(4.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.background)

    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(2.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {
                imageCaching(imageUri = post.userPfp, modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp))

                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                ) {

                    Text(
                        text = post.userName,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = DateUtils.calculateTimeDifference(post.publishedAt) ,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                FeedDropDownMenu(deletePost,edit,post)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            ExpandableText(
                text = post.body,
                modifier = Modifier
                    .fillMaxWidth()
            )


            FlowRow(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 8.dp)
            ) {

                UserPostTags(userPost = post,onTagClicked)

            }

            if(post.attachments.isNotEmpty()){
                when (post.attachments.first().type) {
                    in IMAGE_TYPES -> {
                        ImagePager(images = post.attachments)
                    }

                    else -> {
                        FileMaterial(fileList = post.attachments)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentUser=Firebase.auth.currentUser?.email

                IconButton(onClick = { details(post) }) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = post.replyCount.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(start = 4.dp)
                )

                IconButton(
                    onClick = {
                            onDownVote(post)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = "Down Vote",
                        tint = if (post.downvoted.contains(currentUser)) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }



                Text(
                    text = post.votes.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(end = 4.dp)
                )
                IconButton(
                    onClick = {
                            onUpVote(post)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Upvote",
                        tint = if (post.upvoted.contains(currentUser)) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }


            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedDropDownMenu(
    deletePost: (Post) -> Unit,
    edit: (Post) -> Unit,
    post: Post) {

    var menuExpanded by remember { mutableStateOf(false) }
    val dropdownMenuHeight = 200.dp

    IconButton(onClick = { menuExpanded = !menuExpanded }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More Options"
        )
    }

    AnimatedVisibility(
        visible = menuExpanded,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically(),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier.heightIn(max = dropdownMenuHeight)
        ) {
            DropdownMenuItem(
                onClick = {
                    deletePost(post)
                    menuExpanded = false
                },
                modifier = Modifier.clickable { } ,
                text = { Text(text = "Delete") }
            )

            DropdownMenuItem(
                onClick = {
                    edit(post)
                    menuExpanded = false
                },
                modifier = Modifier.clickable { } ,
                text = { Text(text = "Edit") }
            )

            DropdownMenuItem(
                onClick = {

                    menuExpanded = false
                },
                modifier = Modifier.clickable { } ,
                text = { Text(text = "Save") }
            )

            DropdownMenuItem(
                onClick = {

                    menuExpanded = false
                },
                modifier = Modifier.clickable { } ,
                text = { Text(text = "Report") }
            )
        }
    }
}
