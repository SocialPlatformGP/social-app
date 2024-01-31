package com.gp.posts.presentation.postDetails


import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostItem(/*viewModel: PostDetailsViewModel,*/post:Post) {
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
                        .size(80.dp)
                )

                // User Details Column
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
                        text = post.publishedAt,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                PostDropDownMenu()
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

            // FrameLayout


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Comment Button
                IconButton(onClick = { /* Handle Comment */ }) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Reply Count
                Text(
                    text = "Reply Count",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(start = 4.dp)
                )

                // Upvote Button
                IconButton(onClick = { /* Handle Upvote */ }) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Upvote",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Votes Text
                Text(
                    text = "Votes",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(end = 4.dp)
                )

                // DownVote Button
                IconButton(onClick = { /* Handle DownVote */ }) {
                    Icon(
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
fun PostDetails(post: Post) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(modifier = Modifier.verticalScroll(scrollState)) {

            item {
                PostItem(post = post)
            }

            items(sampleData.replies.size) { post ->
                items(sampleData.replies.size) { nestedItem ->
                    this@LazyColumn.item {
                        NestedRepliesView(sampleData.replies[nestedItem])
                    }
                }
            }
        }
    }
}


@Composable
fun PostDropDownMenu() {

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
            }
        )
        DropdownMenuItem(
            text = {
                Text(text = "Edit")
            },
            onClick = {
            }
        )
        DropdownMenuItem(
            text = {
                Text(text = "Save")
            },
            onClick = {
            }
        )
        DropdownMenuItem(
            text = {
                Text(text = "Report")
            },
            onClick = {
            }
        )
    }
}
val post1 = Post(
    replyCount = 5,
    userName = "JohnDoe",
    userPfp = "user1.jpg",
    id = "post_123",
    authorEmail = "john.doe@example.com",
    publishedAt = "2024-01-30T12:00:00",
    title = "Introduction to Kotlin",
    body = "Kotlin is a modern programming language...",
    votes = 10,
    downvoted = listOf("user2@example.com", "user3@example.com"),
    upvoted = listOf("user4@example.com", "user5@example.com"),
    moderationStatus = "approved",
    editStatus = false,
    tags = listOf(Tag("Programming","#FF0000"), Tag("Kotlin","#ADD8E6")),
    type = "educational",
    attachments = listOf(
        PostAttachment("image1.jpg", "Image"),
        PostAttachment("code_snippet.kt", "Code")
    )
)


@Preview(showSystemUi = true, backgroundColor = 0xFFFEFEFE, showBackground = true, apiLevel = 30)
@Composable
fun ScreenPreview() {
    PostDetails(post = post1)

}

