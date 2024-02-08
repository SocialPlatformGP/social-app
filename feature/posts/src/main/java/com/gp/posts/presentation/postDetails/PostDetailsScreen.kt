package com.gp.posts.presentation.postDetails


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gp.posts.R
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gp.posts.presentation.postsfeed.DropDownMenu
import com.gp.posts.presentation.postsfeed.FileMaterial
import com.gp.posts.presentation.postsfeed.IMAGE_TYPES
import com.gp.posts.presentation.postsfeed.ImagePager
import com.gp.socialapp.model.Post
import com.gp.socialapp.util.DateUtils

@Composable
fun DetailsScreen(viewModel: PostDetailsViewModel, post:Post,edit:(Post)->Unit){
    val statePost by viewModel.currentPost.collectAsState()
    val stateReply by viewModel.currentReplies.collectAsState()
    Log.d("vip", "DetailsScreen:${statePost.id.toString() +stateReply.toString()} ")

    Column {

        DetailsPostItem(viewModel = viewModel, post =post )
        CommentListScreen(nestedReplyItem = stateReply)

    }

    }

@Composable
fun DetailsPostItem(viewModel: PostDetailsViewModel,post:Post) {
    var isUpvoteFilled by remember { mutableStateOf(false) }
    var isDownvoteFilled by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(4.dp)
            .clip(MaterialTheme.shapes.medium)

    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(4.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.userPfp)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.pngwing_com),
                    contentDescription = "picture image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                )


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
                        text = DateUtils.calculateTimeDifference(post.publishedAt),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                DetailsDropDownMenu(viewModel,post)
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

            Spacer(modifier = Modifier.height(8.dp))

            ExpandableText(
                text = post.body,
                modifier = Modifier
                    .fillMaxWidth()
            )

            UserPostTags(userPost = post)

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

                IconButton(onClick = { }) {
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
                        isDownvoteFilled = !isDownvoteFilled
                        if (isDownvoteFilled) {
                            isUpvoteFilled = false
                            viewModel.downVote(post = post)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = "Down Vote",
                        tint = if (isDownvoteFilled) MaterialTheme.colorScheme.primary else Color.Gray
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
                        isUpvoteFilled = !isUpvoteFilled
                        if (isUpvoteFilled) {
                            isDownvoteFilled = false
                            viewModel.upVote(post)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Upvote",
                        tint = if (isUpvoteFilled) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }


            }

        }
    }
}
//@Composable
//fun PostDetails(post: Post) {
//    val scrollState = rememberScrollState()
//    val context = LocalContext.current
//
//    Surface(modifier = Modifier.fillMaxWidth()) {
//        LazyColumn(modifier = Modifier.verticalScroll(scrollState)) {
//            items(sampleData.replies) { reply ->
//                    this@LazyColumn.item {
//                        NestedRepliesView(reply)
//                    }
//                }
//            }
//        }
//    }


@Composable
fun DetailsDropDownMenu(viewModel: PostDetailsViewModel,post: Post) {

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
                    viewModel.deletePost(post)
                    menuExpanded = false
                },
                modifier = Modifier.clickable { } ,
                text = { Text(text = "Delete") }
            )

            DropdownMenuItem(
                onClick = {

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

@Preview(showSystemUi = true, backgroundColor = 0xFFFEFEFE, showBackground = true, apiLevel = 30)
@Composable
fun ScreenPreview() {
  //  PostDetails(post = post1)

}

