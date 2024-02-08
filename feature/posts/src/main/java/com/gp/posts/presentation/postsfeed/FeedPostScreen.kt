package com.gp.posts.presentation.postsfeed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.gp.posts.listeners.OnMoreOptionClicked
import com.gp.posts.presentation.createpost.FlowTags
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag
import com.gp.socialapp.util.DateUtils


@Composable
fun FeedPostScreen(posts: List<Post>) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(posts) { post ->
                FeedPostItem(
                    post = post,
                    {}
                )
            }

        }
    }


}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FeedPostItem(
    post: Post,
    onMoreOptionClicked:(Post) -> Unit
) {
    Card(
        onClick = { /*TODO*/ },
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            TopRow(
                imageUrl = post.userPfp,
                userName = post.userName,
                publishedAt = post.publishedAt,
                editStatus = post.editStatus,
                onMoreOptionClicked = { onMoreOptionClicked(post) }
            )
            PostContent(
                title = post.title,
                body = post.body,
                attachments = post.attachments,
                moderationStatus = post.moderationStatus,

            )
            FlowTags(
                selectedTags = post.tags.toSet(),
                onClick ={ /*TODO*/ }
            )
            ButtomRow(
                upVotes = post.upvoted,
                downVotes = post.downvoted,
                commentCount = post.replyCount,
                votes = post.votes,
                onUpVoteClicked = { /*TODO*/ },
                onDownVoteClicked = { /*TODO*/ },
                onCommentClicked = { /*TODO*/ }

            )


        }
    }

}

@Composable
fun ButtomRow(
    upVotes: List<String>,
    downVotes: List<String>,
    commentCount: Int,
    votes: Int,
    onUpVoteClicked: () -> Unit,
    onDownVoteClicked: () -> Unit,
    onCommentClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {


    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
 fun FlowTags(
    selectedTags: Set<Tag>,
    onClick: (Tag) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.Start
    ) {
        items(selectedTags.toList()) { tag ->
            Chip(onClick = {
                onClick(tag)
            }, colors = ChipDefaults.chipColors(
                backgroundColor = Color(android.graphics.Color.parseColor(tag.hexColor)),
                contentColor = androidx.compose.ui.graphics.Color.White
            )
            ) {
                Text(text = tag.label)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
@Composable
fun PostContent(
    title: String,
    body: String,
    attachments: List<PostAttachment>,
    moderationStatus: String
) {
    Column (
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ){
        Title(title)
        Body(body)
        Attachments(attachments = attachments)
    }
}

@Composable
fun Body(body: String) {
    Text(
        text = body,
    )
}

@Composable
private fun Title(title: String) {
    Text(
        text = title,
    )
}

@Composable
fun Attachments(attachments: List<PostAttachment>) {
    LazyRow{
        items(attachments) { attachment ->
            if(attachment.type == "image"){
                SubcomposeAsyncImage(
                    model = attachment.url,
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .size(150.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            else if(attachment.type == "video"){
                Icon(
                    imageVector = Icons.Filled.VideoFile,
                    contentDescription =null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
            else if (attachment.type == "audio"){
                Icon(
                    imageVector = Icons.Filled.AudioFile,
                    contentDescription =null,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
            else if (attachment.type == "file"){
                Icon(
                    imageVector = Icons.Filled.FileOpen,
                    contentDescription =null,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
            else{
                Text(
                    text = "Unsupported Attachment",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }

        }
    }
}

@Composable
private fun TopRow(
    imageUrl: String,
    userName: String,
    publishedAt: String,
    editStatus: Boolean,
    onMoreOptionClicked: () -> Unit
) {
    //TODO: Add edit status
    Row {
        UserImage(imageUrl)
        Column {
            UserName(userName = userName)
            PostDate(publishedAt = publishedAt)
        }
        Spacer(modifier = Modifier.weight(1f))
        OptionButton {
            onMoreOptionClicked()
        }
    }
}

@Composable
fun OptionButton(

    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)


    ) {
        Icon(
            imageVector = Icons.Default.MoreHoriz,
            contentDescription = "More Options",
        )

    }

}

@Composable
fun PostDate(publishedAt: String) {
    Text(
        text = DateUtils.calculateTimeDifference(publishedAt),
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .padding(
                top = 4.dp,
                start = 8.dp,
                end = 8.dp,
                bottom = 8.dp
            ),
    )
}

@Composable
fun UserName(
    userName: String,
) {
    Text(
        text = userName,
        modifier = Modifier
            .fillMaxWidth(0.7f)

            .padding(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp,
                bottom = 4.dp
            ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        softWrap = true
    )
}

@Composable
fun UserImage(
    imageLink: String
) {
    SubcomposeAsyncImage(
        model = imageLink,
        contentDescription = "User Image",
        modifier = Modifier
            .padding(8.dp)
            .size(48.dp),
        contentScale = ContentScale.Crop,
        ) {
        val state = painter.state
        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "User Image",
            )
        } else {
            SubcomposeAsyncImageContent()
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, apiLevel = 33)
@Composable
fun FeedPostPreview() {
    FeedPostScreen(
        listOf(
            Post(
                id = "1",
                title = "Title",
                body = "Bofgchvbjnjkdv,ffffffffffffffffffffffffffffs.aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.dy",
                authorEmail = "authorEmail",
                publishedAt = "Sun Feb 04 16:47:40 GMT+02:00 2024",
                userName = "usGGGGGGffffffffffffgggggggggJJJJJJJName",
            )
        )
    )
}
