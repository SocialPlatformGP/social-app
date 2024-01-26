package com.gp.posts.presentation.postsSearch.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gp.posts.presentation.postsSearch.SearchResultsViewModel
import com.gp.socialapp.model.Post
import com.gp.posts.R



@Composable
fun SearchScreen(viewModel: SearchResultsViewModel) {
    val searchResult by viewModel.searchResult.collectAsState(initial = emptyList())

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Gray
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
                
            }
            Text(
                text = "Search posts",
                style = typography.h6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            LazyColumn(

                modifier = Modifier
                    .fillMaxWidth()

            ) {
                items(searchResult.size) { index ->
                    val post = searchResult[index]
                    PostItem(post = post)
                }
            }

        }
    }


@Composable
fun PostItem(post: Post) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // to download the image
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.userPfp)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.pngwing_com),
                    contentDescription = "picture image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clip(CircleShape)
                )


                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = post.userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = post.publishedAt,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Body
            Text(
                text = post.body,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            // Title
            Text(
                text = post.title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                post.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                            .clickable { /*handle click ba3den*/ }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = tag.label, color = Color(android.graphics.Color.parseColor(tag.hexColor)))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Like Button
                var click=false
                IconButton(
                    onClick = {  click=true  }
                ) {
                    val icon = if (click) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp
                    Icon(imageVector = icon, contentDescription = "Like")
                }

                // Like Count
                Text(
                    text = post.votes.toString(),
                    modifier = Modifier.padding(end = 4.dp),
                    color = Color.Gray
                )
                var clicked=false

                // Dislike Button
                IconButton(
                    onClick = { clicked=true }
                ) {
                    val icon = if (clicked) Icons.Default.ThumbDown else Icons.Default.ThumbUp
                }


                // Comment Button
                IconButton(
                    onClick = { /* Handle comment */ }
                ) {
                    Icon(imageVector = Icons.Default.Comment, contentDescription = "Comment")
                }


                // More Options Button
                IconButton(
                    onClick = { /* Handle more options */ }
                ) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Options")
                }
            }


            // Reply Count
            Text(
                text = post.replyCount.toString(),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                color = Color.Gray
            )
        }
    }
}
