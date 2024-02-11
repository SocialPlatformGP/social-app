package com.gp.posts.presentation.postsfeed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.posts.presentation.feedUiEvents.PostEvent
import com.gp.socialapp.database.model.MimeType
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag
import com.gp.socialapp.util.DateUtils


@Composable
fun FeedPostScreen(
    posts: List<Post>,
    postEvent: (PostEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(posts) { post ->
                FeedPostItem(
                    post = post,
                    postEvent = postEvent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FeedPostItem(
    post: Post,
    postEvent: (PostEvent) -> Unit
) {
    Card(
        onClick = { postEvent(PostEvent.OnPostClicked(post)) },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                8.dp
            )
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
                onEditPostClicked = { postEvent(PostEvent.OnPostEdited(post)) },
                onDeletePostClicked = { postEvent(PostEvent.OnPostDeleted(post)) }

            )
            PostContent(
                title = post.title,
                body = post.body,
                attachments = post.attachments,
                moderationStatus = post.moderationStatus,
                postEvent = postEvent

            )
            FlowTags(
                selectedTags = post.tags.toSet(),
                onTagClicked = { postEvent(PostEvent.OnTagClicked(it)) }
            )
            BottomRow(
                upVotes = post.upvoted,
                downVotes = post.downvoted,
                commentCount = post.replyCount,
                votes = post.votes,
                onUpVoteClicked = { postEvent(PostEvent.OnPostUpVoted(post)) },
                onDownVoteClicked = { postEvent(PostEvent.OnPostDownVoted(post)) },
                onCommentClicked = {  postEvent(PostEvent.OnCommentClicked(post)) }
            )
        }
    }

}

@Composable
fun BottomRow(
    upVotes: List<String>,
    downVotes: List<String>,
    commentCount: Int,
    votes: Int,
    onUpVoteClicked: () -> Unit,
    onDownVoteClicked: () -> Unit,
    onCommentClicked: () -> Unit
) {
    val currentEmail = Firebase.auth.currentUser?.email
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            onClick = onUpVoteClicked,
            modifier = Modifier.background(
                color = Color.LightGray.copy(alpha = 0.6f),
                shape = RoundedCornerShape(
                    topStart = 32.dp,
                    bottomStart = 32.dp
                )
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowUp,
                    contentDescription = "UpVote",
                    tint = if (upVotes.contains(currentEmail)) Color.Green else Color.Unspecified
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = votes.toString()
                )
            }
        }
        Spacer(modifier = Modifier.width(1.dp))
        IconButton(
            onClick = onDownVoteClicked,
            modifier = Modifier.background(
                color = Color.LightGray.copy(alpha = 0.6f),
                shape = RoundedCornerShape(
                    topEnd = 32.dp,
                    bottomEnd = 32.dp
                )
            )
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowDown,
                contentDescription = "DownVote",
                modifier = Modifier.padding(12.dp),
                tint = if (downVotes.contains(currentEmail)) Color.Red else Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = onCommentClicked,
            modifier = Modifier.background(
                color = Color.LightGray.copy(alpha = 0.6f),
                shape = RoundedCornerShape(32.dp)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "UpVote"
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = commentCount.toString()
                )
            }
        }


    }
}

@Composable
fun FlowTags(
    selectedTags: Set<Tag>,
    onTagClicked: (Tag) -> Unit,
) {
    if(selectedTags.isEmpty()) return
    LazyRow(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(8.dp)
    ) {
        items(selectedTags.toList()) { tag ->
            TagItem(onTagClicked, tag)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun TagItem(
    onTagClicked: (Tag) -> Unit,
    tag: Tag
) {
    Chip(
        onClick = {
            onTagClicked(tag)
        }, colors = ChipDefaults.chipColors(
            backgroundColor = Color(android.graphics.Color.parseColor(tag.hexColor)),
            contentColor = Color.White
        )
    ) {
        Text(text = tag.label)
    }
    Spacer(modifier = Modifier.width(8.dp))
}

@Composable
fun PostContent(
    title: String,
    body: String,
    attachments: List<PostAttachment>,
    moderationStatus: String,
    postEvent: (PostEvent) -> Unit
) {
    Column(
        Modifier
            .padding(
                top = 8.dp,
                bottom = 8.dp
            )
            .fillMaxWidth()
    ) {
        Title(title)
        Body(body)
        Attachments(
            attachments = attachments,
            onAudioClicked = { postEvent(PostEvent.OnAudioClicked(it)) },
            onVideoClicked = { postEvent(PostEvent.OnVideoClicked(it)) },
            onImageClicked = { postEvent(PostEvent.OnImageClicked(it)) },
            onDocumentClicked = { postEvent(PostEvent.OnDocumentClicked(it)) }
        )
    }
}

@Composable
fun Body(body: String) {
    if (body.isEmpty()) return
    ExpandableText(
        text = body,
        maxLinesCollapsed = 3,
    )
}

@Composable
fun ExpandableText(
    text: String,
    maxLinesCollapsed: Int,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth().padding(
        start = 8.dp,
        end = 8.dp,
        top = 2.dp,
        bottom = 4.dp
    )) {
        Text(
            text = text,
            maxLines = if (expanded) Int.MAX_VALUE else maxLinesCollapsed,
            modifier = modifier.clickable { expanded = !expanded },
            overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
        )
        if (expanded) {
            IconButton(
                onClick = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }

}

@Composable
private fun Title(title: String) {
    if (title.isEmpty()) return
    Text(
        text = title,
        modifier = Modifier.padding(
            start = 8.dp,
            end = 8.dp,
            top = 4.dp,
            bottom = 2.dp
        ),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
}

@Composable
fun Attachments(
    attachments: List<PostAttachment>,
    onAudioClicked: (PostAttachment) -> Unit,
    onVideoClicked: (PostAttachment) -> Unit,
    onImageClicked: (PostAttachment) -> Unit,
    onDocumentClicked: (PostAttachment) -> Unit
) {

    LazyRow (
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        items(attachments) { attachment ->
            AttachmentItem(
                attachment,
                onVideoClicked = { onVideoClicked(attachment) },
                onAudioClicked = { onAudioClicked(attachment) },
                onImageClicked = { onImageClicked(attachment) },
                onDocumentClicked = { onDocumentClicked(attachment) }
            )

        }
    }
}

@Composable
private fun AttachmentItem(
    attachment: PostAttachment,
    onVideoClicked: () -> Unit,
    onAudioClicked: () -> Unit,
    onImageClicked: () -> Unit,
    onDocumentClicked: () -> Unit
) {
    val width = LocalConfiguration.current.screenWidthDp.dp
    Box (
        modifier = Modifier
            .size(width = width, height = 300.dp)
    ){
        var icon by remember { mutableStateOf(Icons.Filled.Image) }
        when (attachment.type) {
            in listOf(
                MimeType.VIDEO.readableType,
                MimeType.MKV.readableType,
                MimeType.AVI.readableType,
                MimeType.MP4.readableType,
                MimeType.MOV.readableType,
                MimeType.WMV.readableType
            ) -> {
                Icon(
                    imageVector = Icons.Filled.VideoLibrary,
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 200.dp, height = 300.dp)
                        .clickable {
                            onVideoClicked()
                        }
                        .align(Alignment.Center)
                )
            }

            in listOf(
                MimeType.PDF.readableType,
                MimeType.DOCX.readableType,
                MimeType.XLSX.readableType,
                MimeType.PPTX.readableType,
            ) -> {
                Icon(
                    imageVector = Icons.Filled.InsertDriveFile,
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 200.dp, height = 300.dp)
                        .clickable {
                            onDocumentClicked()
                        }
                        .align(Alignment.Center)
                )
            }

            in listOf(
                MimeType.JPEG.readableType,
                MimeType.PNG.readableType,
                MimeType.GIF.readableType,
                MimeType.BMP.readableType,
                MimeType.WEBP.readableType
            ) -> {
                icon = Icons.Filled.Image
                SubcomposeAsyncImage(
                    model = attachment.url,
                    contentDescription = null,
                    modifier = Modifier
                        .height(300.dp)
                        .clickable {
                            onImageClicked()
                        }
                        .align(Alignment.Center)
                    ,
                    contentScale = ContentScale.FillBounds,

                    ) {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                        CircularProgressIndicator()
                    } else {
                        SubcomposeAsyncImageContent()
                    }
                }
            }

            in listOf(
                MimeType.AUDIO.readableType,
                MimeType.MP3.readableType,
                MimeType.AAC.readableType,
                MimeType.WAV.readableType,
                MimeType.OGG.readableType,
                MimeType.FLAC.readableType
            ) -> {
                icon = Icons.Filled.MusicNote
            }

            else -> {
                Icon(
                    imageVector = Icons.Filled.InsertDriveFile,
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 200.dp, height = 300.dp)
                        .clickable {
                            onAudioClicked()
                        }
                        .align(Alignment.Center)
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
    onEditPostClicked: () -> Unit,
    onDeletePostClicked: () -> Unit

) {
    //TODO: Add edit status
    Row {
        UserImage(imageUrl)
        Column {
            UserName(userName = userName)
            PostDate(publishedAt = publishedAt)
        }
        Spacer(modifier = Modifier.weight(1f))

        OptionButton (
            onEditPostClicked = onEditPostClicked,
            onDeletePostClicked = onDeletePostClicked
        )
    }
}
@Composable
fun OptionButton(
    onEditPostClicked: () -> Unit,
    onDeletePostClicked: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(
            onClick = {expanded = !expanded},
            modifier = Modifier
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "More Options",
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                clippingEnabled = true

            )
        ) {
            DropdownMenuItem(
                onClick = {
                    onEditPostClicked()
                    expanded = false
                }
            ) {
                Text(text = "Edit")
            }
            DropdownMenuItem(
                onClick = {
                    onDeletePostClicked()
                    expanded = false
                }
            ) {
                Text(text = "Delete")
            }

        }
    }
}

@Composable
fun PostDate(publishedAt: String) {
    Text(
        text = DateUtils.calculateTimeDifference(publishedAt)+" ago",
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .padding(
                top = 2.dp,
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
                bottom = 2.dp
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
            .size(40.dp)
            .clip(CircleShape),
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
    UserImage(imageLink = "")
}
