package com.gp.posts.presentation.postDetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
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
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
                    , verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = reply?.authorEmail ?: "Unknown", fontWeight = FontWeight.Bold)
                IconButton(onClick = { isEditing = !isEditing }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
            }

            Text(text = reply?.content ?: "No content available")

            // Reply actions (upvote, downvote, etc.) can be added here

            if (isEditing) {
                EditableReplyContent(
                    editedContent = editedContent,
                    onEditContentChange = { editedContent = it },
                    onSaveClick = {
                        // Handle save click
                        isEditing = false
                    }
                )
            }
        }
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