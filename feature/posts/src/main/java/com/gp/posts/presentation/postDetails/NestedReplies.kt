package com.gp.posts.presentation.postDetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.times
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gp.posts.R
import com.gp.socialapp.model.NestedReplyItem
import com.gp.socialapp.model.Reply

data class Reply(
    val id: String = "",
    val authorEmail: String,
    val postId: String,
    val parentReplyId: String?,
    val content: String,
    val votes: Int = 0,
    val depth: Int,
    val createdAt: String?,
    val deleted: Boolean = false,
    val upvoted: List<String> = emptyList(),
    val downvoted: List<String> = emptyList(),
    val collapsed: Boolean = false,
    val editStatus: Boolean = false
)

@Composable
fun NestedRepliesView(nestedReplyItem: NestedReplyItem,viewModel: PostDetailsViewModel) {
   // ReplyItem(nestedReplyItem.reply)
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (nestedReplyItem.reply?.depth ?: 0) * 16.dp)
            .background(Color.Gray.copy(alpha = 0.1f))
            .padding(8.dp)
    ) {

        items(nestedReplyItem.replies) { nestedItem ->
            this@LazyColumn.item {
               // ReplyItem(nestedItem.reply)
            }
        }
    }
}

@Composable
fun ReplyItem(reply: Reply, viewModel: PostDetailsViewModel) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(BorderStroke(1.dp, Color.Gray), shape = MaterialTheme.shapes.medium)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Image
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("")
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.pngwing_com),
                    contentDescription = "picture image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                )

                // Add spacing between image and the text
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = reply?.authorEmail ?: "Unknown",
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = reply?.createdAt.toString(),
                )

                // Add more spacing if needed
                Spacer(modifier = Modifier.width(180.dp))

                ReplyDropDownMenu(viewModel=viewModel,reply = reply)
            }



            ExpandableText(text = reply?.content.toString())

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


                // Upvote Button
                IconButton(onClick = { viewModel.replyUpVote(reply)}) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Upvote",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Votes Text
                Text(
                    text = reply.votes.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(end = 4.dp)
                )

                // DownVote Button
                IconButton(onClick = { viewModel.replyDownVote(reply = reply) }) {
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
fun ReplyDropDownMenu(viewModel: PostDetailsViewModel,reply: Reply) {

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
                viewModel.deleteReply(reply)
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
                Text(text = "Report")
            },
            onClick = {
                //to do
            }
        )
    }
}



@Preview(apiLevel = 29)
@Composable
fun PreviewNestedRepliesView() {


}