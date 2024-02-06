package com.gp.posts.presentation.postsfeed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gp.posts.R
import com.gp.posts.presentation.postDetails.ExpandableText
import com.gp.posts.presentation.postDetails.UserPostTags
import com.gp.socialapp.model.Post
import com.gp.socialapp.util.DateUtils

@Composable
fun VipScreen(viewModel: VipFeedViewModel,details:(Post)->Unit,edit:(Post)->Unit){
    val state by viewModel.uiState.collectAsState()
    LazyColumn( modifier = Modifier.fillMaxWidth(1f)){
        items(state.posts){
            PostItem(viewModel =viewModel ,it, details =details , edit = edit )

        }

    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostItem(viewModel: VipFeedViewModel, post: Post,details:(Post)->Unit,edit:(Post)->Unit) {
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
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                // Circular Image
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
                DropDownMenu(viewModel,post, edit = edit)
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


            FlowRow(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 8.dp)
            ) {

                UserPostTags(userPost = post)

            }
            if(post.attachments.isNotEmpty()){
                if (post.attachments.first().type=="IMAGE"){
                    ImagePager(images = post.attachments)
                }
                else{
                    FileMaterial(fileList = post.attachments, open = { })
                }

            }



            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Comment Button
                IconButton(onClick = { details(post)}) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Reply Count
                Text(
                    text = post.replyCount.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(start = 4.dp)
                )

                IconButton(
                    onClick = {
                        isUpvoteFilled = !isUpvoteFilled
                        if (isUpvoteFilled) {
                            isDownvoteFilled = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Upvote",
                        tint = if (isUpvoteFilled) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }

                // Votes Text
                Text(
                    text = post.votes.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(end = 4.dp)
                )

                IconButton(
                    onClick = {
                        isDownvoteFilled = !isDownvoteFilled
                        if (isDownvoteFilled) {
                            isUpvoteFilled = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = "Down Vote",
                        tint = if (isDownvoteFilled) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }

        }
    }
}

@Composable
fun DropDownMenu(viewModel: VipFeedViewModel,post: Post,edit:(Post)->Unit) {

    var menuExpanded by remember { mutableStateOf(false) }
    IconButton(onClick = { menuExpanded = !menuExpanded }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More Options"
        )
    }
    DropdownMenu(
        expanded=menuExpanded
        , onDismissRequest = { menuExpanded = false }
    ) {
        DropdownMenuItem(
            text = {
                Text(text = "Delete")
            },
            onClick = {
                viewModel.deletePost(post = post)
            }
        )
        DropdownMenuItem(
            text = {
                Text(text = "Edit")
            },
            onClick = {
                edit(post)

            }
        )
        DropdownMenuItem(
            text = {
                Text(text = "Save")
            },
            onClick = {
                //to do
            }
        )
        DropdownMenuItem(
            text = {
                Text(text = "Report")
            },
            onClick = {
                // to do
            }
        )
    }
}

