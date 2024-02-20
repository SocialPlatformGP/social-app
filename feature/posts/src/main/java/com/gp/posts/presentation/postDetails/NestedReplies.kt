package com.gp.posts.presentation.postDetails


import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gp.posts.fontFamily
import com.gp.posts.fontFamily2
import com.gp.posts.presentation.feedUiEvents.ReplyEvent
import com.gp.posts.presentation.postsfeed.UserImage
import com.gp.socialapp.model.NestedReplyItem
import com.gp.socialapp.model.Reply
import com.gp.socialapp.theme.AppTheme
import com.gp.socialapp.util.DateUtils


fun LazyListScope.commentList(comments: List<NestedReplyItem>, level: Int = 0,onReplyEvent: (ReplyEvent)->Unit) {
    comments.forEach { comment ->
        commentItem(comment, level,onReplyEvent)
    }
}

fun LazyListScope.commentItem(comment: NestedReplyItem, level: Int,onReplyEvent: (ReplyEvent)->Unit) {

    item {
        val ltrLayoutDirection = remember { LayoutDirection.Ltr }
        CompositionLocalProvider(LocalLayoutDirection provides ltrLayoutDirection) {
            ReplyItem(comment, level,onReplyEvent)
        }
    }
    commentList(comment.replies, level + 1,onReplyEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ReplyItem(
    comment: NestedReplyItem, level: Int,
    replyEvent: (ReplyEvent)->Unit
) {
    if (comment.reply == null) return
    val padding = with(LocalDensity.current) { 16.dp.toPx() }
    Column {
        val color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
        androidx.compose.material3.Card(
            modifier = Modifier
                .drawBehind {
                    repeat(level + 1) {
                        drawLine(
                            color = color.copy(alpha = 1f),
                            start = Offset(it * padding, 0f),
                            end = Offset(it * padding, size.height),
                            strokeWidth = 2f
                        )
                    }
                }
                .padding(start = (16.dp * level) + 8.dp, end = 8.dp),
            shape = ShapeDefaults.Medium,
            border = BorderStroke(1.dp, Color.Gray),
            colors = CardDefaults.cardColors(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary,

            )

            ) {
            Column(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //TODO: Add user profile pic here
                    UserImage(
                        imageLink = comment.reply?.userPfp ?: "",
                        size = 26.dp
                    )
                    Text(
                        text = if ((comment.reply?.authorName?.length
                                ?: 0) > 10
                        ) comment.reply?.authorName?.substring(
                            0,
                            10
                        ) ?: "" else comment.reply?.authorName ?: "Unknown",
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 4.dp
                            ),
                        overflow = if ((comment.reply?.authorName?.length ?: 0) > 10) {
                            TextOverflow.Ellipsis
                        } else TextOverflow.Clip,
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = DateUtils.calculateTimeDifference(comment.reply?.createdAt!!),
                        modifier = Modifier
                            .padding(
                                start = 4.dp,
                                end = 8.dp
                            ),
                        color = Color.Gray,
                        overflow = if ((comment.reply?.createdAt?.length ?: 0) > 10) {
                            TextOverflow.Ellipsis
                        } else TextOverflow.Clip,
                        fontFamily = fontFamily2,
                        fontSize = 12.sp
                    )

                }

                Text(
                    text = comment.reply?.content ?: "No body",
                    modifier = Modifier
                        .padding(
                            start = 8.dp,
                            end = 4.dp,
                            top = 4.dp
                        ),
                    fontSize = 14.sp,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 4.dp,
                            end = 4.dp,
                        )
                        .sizeIn(maxHeight = 28.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        var visible by remember { mutableStateOf(false) }
                        IconButton(
                            onClick = {
                                visible = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "More options"
                            )
                        }
                        DropdownMenu(
                            expanded = visible,
                            onDismissRequest = { visible = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = { replyEvent(ReplyEvent.OnReplyEdited(reply = comment.reply!!)) }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = { replyEvent(ReplyEvent.OnReplyDeleted(reply = comment.reply!!)) }
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            replyEvent(ReplyEvent.OnAddReply(reply = comment.reply!!))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Comment,
                            contentDescription = "Add a comment"
                        )
                    }
                    IconButton(
                        onClick = {
                            replyEvent(ReplyEvent.OnReplyUpVoted(reply = comment.reply!!))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ThumbUp,
                            contentDescription = "Like"
                        )
                    }
                    Text(text = "0")
                    IconButton(
                        onClick = {
                            replyEvent(ReplyEvent.OnReplyDownVoted(reply = comment.reply!!))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ThumbDown,
                            contentDescription = "Share"
                        )
                    }
                }


            }

        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}


@Preview(showSystemUi = true, showBackground = true, locale = "en-US")
@Composable
fun NestedReplyItemPreview() {
    AppTheme {
        Surface {
            ReplyItem(
                comment = sampleData.replies[0] ,
                level = 0,
                replyEvent = { ReplyEvent.Initial }
            )
        }
    }


}
@Preview(showSystemUi = true, showBackground = true, locale = "en-US",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun NestedReplyItemPreviewNight() {
    AppTheme {
        Surface {
            ReplyItem(
                comment = sampleData.replies[0] ,
                level = 0,
                replyEvent = { ReplyEvent.Initial }
            )
        }
    }

}

val sampleData = NestedReplyItem(
    reply = null,
    replies = listOf(
        NestedReplyItem(
            reply =Reply(
                authorEmail = "Alddddddddddddddddddddddddddddddddddddddddddddice",
                postId = "123",
                parentReplyId = "1",
                content = "Nested Reply 1",
                depth = 1, createdAt = "4d"
            ),
            replies = emptyList()
        ),
        NestedReplyItem(
            reply = Reply(
                authorEmail = "Bo3b",
                postId = "123",
                parentReplyId = "2",
                content = "Nested Reply 2",
                depth = 1, createdAt = "2d"
            ),
            replies = emptyList()
        )
    )
)

