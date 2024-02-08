package com.gp.posts.presentation.postDetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.gp.socialapp.model.NestedReplyItem
import com.gp.socialapp.util.DateUtils

@Composable
fun CommentListScreen(nestedReplyItem: NestedReplyItem) {

    Box {
        LazyColumn(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxSize(),
        ) {
            commentList(comments = listOf(nestedReplyItem))
        }
        if (nestedReplyItem.replies.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

fun LazyListScope.commentList(comments: List<NestedReplyItem>, level: Int = 0) {
    comments.forEach { comment ->
        commentItem(comment, level)
    }
}

fun LazyListScope.commentItem(comment: NestedReplyItem, level: Int) {
    item {
        val ltrLayoutDirection = remember { LayoutDirection.Ltr }
        CompositionLocalProvider (LocalLayoutDirection provides ltrLayoutDirection){
            ReplyItem(comment, level)
        }

    }
    commentList(comment.replies, level + 1)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ReplyItem(comment: NestedReplyItem, level: Int) {
    val padding = with(LocalDensity.current) { 16.dp.toPx() }
    val o = LayoutDirection.Ltr
    Card(
        onClick = { /*TODO*/ },
        modifier = Modifier

            .drawBehind {
                repeat(level + 1) {
                    drawLine(
                        color = Color.Red.copy(alpha = 1f),
                        start = Offset(it * padding, 0f),
                        end = Offset(it * padding, size.height),
                        strokeWidth = 2f
                    )
                }
            }
            .padding(start = (16.dp * level) + 8.dp, end = 8.dp, bottom = 4.dp)
            .fillMaxSize(),
        shape = ShapeDefaults.Medium,
        elevation = 0.dp,
        border = BorderStroke(1.dp, Color.Gray),
        backgroundColor = Color.Gray.copy(alpha = 0.1f)

    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                //TODO: Add user profile pic here
                Image(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Random image",
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),//TODO: this color for testing only
                )
                Text(
                    text = comment.reply?.authorEmail ?: "Unknown",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .padding(4.dp)
                )
                Text(
                    text = DateUtils.calculateTimeDifference(comment.reply?.createdAt.toString()) ,
                    modifier = Modifier.padding(4.dp)
                )

            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ){
                Text(
                    text = comment.reply?.content ?: "No body",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription ="More options"
                    )
                }
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Comment,
                        contentDescription ="Add a comment"
                    )
                }
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ThumbUp,
                        contentDescription ="Like"
                    )
                }
                Text(text = "0")
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ThumbDown,
                        contentDescription ="Share"
                    )
                }
            }

        }

    }

}


@Preview(showSystemUi = true, showBackground = true, locale = "en-US")
@Composable
fun NestedReplyItemPreview() {

}
