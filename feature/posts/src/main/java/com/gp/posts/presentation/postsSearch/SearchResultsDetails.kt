package com.gp.posts.presentation.postsSearch

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gp.socialapp.model.Post
import com.gp.posts.R
import com.gp.posts.presentation.postDetails.ExpandableText
import com.gp.posts.presentation.postDetails.UserPostTags
import com.gp.socialapp.util.DateUtils


@Composable
fun SearchResultScreen(viewModel: SearchResultsViewModel,searchQuery:String,isTag:Boolean) {
    if(isTag){
        viewModel.searchPostsByTag(searchQuery)
    }
    else{
        viewModel.searchPostsByTitle(searchQuery)
    }

    val state by viewModel.searchResult.collectAsState()
    LazyColumn( modifier = Modifier.fillMaxWidth(1f)){
        items(state){
            PostItem(viewModel =viewModel ,it )

        }

    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostItem(viewModel: SearchResultsViewModel, post: Post) {
    androidx.compose.material3.Card(
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

                    androidx.compose.material3.Text(
                        text = post.userName,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    androidx.compose.material3.Text(
                        text = DateUtils.calculateTimeDifference(post.publishedAt),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                DropDownMenu(viewModel, post)
            }

            Spacer(modifier = Modifier.height(8.dp))

            androidx.compose.material3.Text(
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Comment Button
                androidx.compose.material3.IconButton(onClick = {  }) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Reply Count
                androidx.compose.material3.Text(
                    text = post.replyCount.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(start = 4.dp)
                )

                // Upvote Button
                androidx.compose.material3.IconButton(onClick = { viewModel.upVote(post) }) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Upvote",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Votes Text
                androidx.compose.material3.Text(
                    text = post.votes.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(end = 4.dp)
                )

                // DownVote Button
                androidx.compose.material3.IconButton(onClick = { viewModel.downVote(post = post) }) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = "Down Vote",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

        }
    }
}

@Composable
fun DropDownMenu(viewModel: SearchResultsViewModel, post: Post) {

    var menuExpanded by remember { mutableStateOf(false) }
    androidx.compose.material3.IconButton(onClick = { menuExpanded = !menuExpanded }) {
        androidx.compose.material3.Icon(
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
                androidx.compose.material3.Text(text = "Delete")
            },
            onClick = {
                viewModel.deletePost(post = post)
            }
        )
        DropdownMenuItem(
            text = {
                androidx.compose.material3.Text(text = "Edit")
            },
            onClick = {

            }
        )
        DropdownMenuItem(
            text = {
                androidx.compose.material3.Text(text = "Save")
            },
            onClick = {
                //to do
            }
        )
        DropdownMenuItem(
            text = {
                androidx.compose.material3.Text(text = "Report")
            },
            onClick = {
                // to do
            }
        )
    }
}
