package com.gp.posts.presentation.postDetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.More
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class NestedReplyItem(
    var reply: Reply?,
    var replies: List<NestedReplyItem>
)

@Composable
fun NestedRepliesView(nestedReplyItem: NestedReplyItem) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (nestedReplyItem.reply?.depth ?: 0) * 16.dp)
            .background(Color.Gray.copy(alpha = 0.1f))
            .padding(8.dp)
    ) {
        item {
            ReplyItem(nestedReplyItem.reply)
        }

        items(nestedReplyItem.replies.size) { nestedItem ->
            this@LazyColumn.item {
                NestedRepliesView(nestedReplyItem.replies[nestedItem])
            }
        }
    }
}

@Composable
fun ReplyItem(reply: Reply?) {
    var isEditing by remember { mutableStateOf(false) }
    var editedContent by remember { mutableStateOf(reply?.content ?: "") }

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

                // Add spacing between the text and timestamp
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "4d",
                )

                // Add more spacing if needed
                Spacer(modifier = Modifier.width(180.dp))

                ReplyDropDownMenu()
            }



            ExpandableText(text = "how are you my friend today")

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
                IconButton(onClick = {  }) {
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
fun ReplyDropDownMenu() {

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
                Text(text = "Report")
            },
            onClick = {
            }
        )
    }
}

@Composable
fun EditableReplyContent(
    editedContent: String,
    onEditContentChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    if (isEditing) {
        BasicTextField(
            value = editedContent,
            onValueChange = { onEditContentChange(it) },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSaveClick()
                    isEditing = false

                }
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    // Handle edit click
                    isEditing = true
                }
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }

            IconButton(
                onClick = {
                    // Handle send click
                    onSaveClick()
                }
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

val sampleData = NestedReplyItem(
    reply = Reply(
        authorEmail = "John Doe",
        postId = "123",
        parentReplyId = null,
        content = "This is the main reply content",
        depth = 0, createdAt = "6d"
    ),
    replies = listOf(
        NestedReplyItem(
            reply = Reply(
                authorEmail = "Alice",
                postId = "123",
                parentReplyId = "1",
                content = "Nested Reply 1",
                depth = 1, createdAt = "4d"
            ),
            replies = emptyList()
        ),
        NestedReplyItem(
            reply = Reply(
                authorEmail = "Bob",
                postId = "123",
                parentReplyId = "2",
                content = "Nested Reply 2",
                depth = 1, createdAt = "2d"
            ),
            replies = emptyList()
        )
    )
)

@Preview(apiLevel = 29)
@Composable
fun PreviewNestedRepliesView() {
    NestedRepliesView(nestedReplyItem = sampleData)


}