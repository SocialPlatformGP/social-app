package com.gp.posts.presentation.postsfeed

//import com.gp.posts.fontFamily2
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.twotone.KeyboardDoubleArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.gp.posts.R
import com.gp.posts.adapter.ImagePagerZ
import com.gp.posts.fontFamily
import com.gp.posts.fontFamily2
import com.gp.posts.merriweatherFamilyBody
import com.gp.posts.montserratFontFamily
import com.gp.posts.presentation.feedUiEvents.PostEvent
import com.gp.socialapp.database.model.MimeType
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag
import com.gp.socialapp.theme.AppTheme
import com.gp.socialapp.util.DateUtils
import java.util.Locale


@Composable
fun FeedPostScreen(
    posts: List<Post>,
    postEvent: (PostEvent) -> Unit,
    currentEmail: String

) {

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            items(posts) { post ->
                FeedPostItem(
                    post = post,
                    postEvent = postEvent,
                    currentEmail = currentEmail
                )
                Spacer(modifier = Modifier.size(6.dp))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPostItem(
    post: Post,
    postEvent: (PostEvent) -> Unit,
    currentEmail: String
) {
    androidx.compose.material3.Card(
        onClick = { postEvent(PostEvent.OnPostClicked(post)) },
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondary,

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
            FlowTags(
                selectedTags = post.tags.toSet(),
                onTagClicked = { postEvent(PostEvent.OnTagClicked(it)) }
            )
            PostContent(
                title = post.title,
                body = post.body,
                attachments = post.attachments,
                moderationStatus = post.moderationStatus,
                postEvent = postEvent

            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 8.dp, end = 8.dp, bottom = 8.dp
                    ),
                thickness = 0.5.dp,
                color = Color.Gray
            )
            BottomRow(
                upVotes = post.upvoted,
                downVotes = post.downvoted,
                commentCount = post.replyCount,
                votes = post.votes,
                filesCount = post.attachments.size,
                onUpVoteClicked = { postEvent(PostEvent.OnPostUpVoted(post)) },
                onDownVoteClicked = { postEvent(PostEvent.OnPostDownVoted(post)) },
                onCommentClicked = {
                    postEvent(PostEvent.OnCommentClicked(post))
                },
                currentEmail = currentEmail,
                onShowFilesClicked = { postEvent(PostEvent.OnViewFilesAttachmentClicked(post.attachments)) }
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
    filesCount: Int = 0,
    onUpVoteClicked: () -> Unit,
    onDownVoteClicked: () -> Unit,
    onCommentClicked: () -> Unit,
    currentEmail: String,
    onShowFilesClicked: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 8.dp, end = 8.dp, bottom = 8.dp
            )
            .sizeIn(
                maxHeight = 35.dp
            ),
        horizontalArrangement = Arrangement.Start
    ) {
        OutlinedButton(
            onClick = onUpVoteClicked,
            contentPadding = PaddingValues(6.dp),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
        ) {
            Icon(
                imageVector = Icons.TwoTone.KeyboardDoubleArrowUp,
                contentDescription = "UpVote",
                tint = if (upVotes.contains(currentEmail)) Color.Green else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .clickable {
                        onUpVoteClicked()
                    },
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = votes.toString(),
                style = TextStyle(
                    fontFamily = montserratFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.sizeIn(
                    minWidth = 20.dp
                ),
                textAlign = TextAlign.Center,
                color = when {
                    upVotes.contains(currentEmail) -> Color.Green
                    downVotes.contains(currentEmail) -> Color.Red
                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                },
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Filled.KeyboardDoubleArrowDown,
                contentDescription = "DownVote",
                tint = if (downVotes.contains(currentEmail)) Color.Red else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .clickable {
                        onDownVoteClicked()
                    }
            )


        }
        Spacer(modifier = Modifier.width(16.dp))
        OutlinedButton(
            onClick = onCommentClicked,
            contentPadding = PaddingValues(
                horizontal = 12.dp,
            ),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),

            ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Chat,
                    contentDescription = "UpVote",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = commentCount.toString(),
                    style = TextStyle(
                        fontFamily = montserratFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        if(filesCount > 0) {
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(
                onClick = onShowFilesClicked,
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                ),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),

                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.attachment_plus_svgrepo_com),
                        contentDescription = "UpVote",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = filesCount.toString(),
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = { /*TODO  handle share button in post item*/ },
            contentPadding = PaddingValues(),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),

            ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "UpVote",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowTags(
    selectedTags: Set<Tag>,
    onTagClicked: (Tag) -> Unit,
) {
    if (selectedTags.isEmpty()) return
    FlowRow(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        selectedTags.toList().forEach { tag ->
            TagItem(onTagClicked, tag)

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TagItem(
    onTagClicked: (Tag) -> Unit,
    tag: Tag
) {
    val backgroundColor = Color(android.graphics.Color.parseColor(tag.hexColor))
    Chip(
        onClick = {
            onTagClicked(tag)
        }, colors = ChipDefaults.chipColors(
            backgroundColor = backgroundColor,
            contentColor = Color.White,

            ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .sizeIn(
                maxHeight = 24.dp,
            )
            .padding(2.dp)

    ) {
        Text(
            text = tag.label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
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
            .fillMaxWidth()
    ) {
        Title(title)
        Body(body)
        Attachments(
            attachments = attachments,
            postEvent = postEvent
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 8.dp, end = 8.dp, top = 2.dp, bottom = 4.dp
            )
    ) {
        Text(
            text = text.capitalize(Locale.ROOT),
            maxLines = if (expanded) Int.MAX_VALUE else maxLinesCollapsed,
            modifier = modifier.clickable { expanded = !expanded },
            overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
            fontFamily = merriweatherFamilyBody,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.outline
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
        text = title.capitalize(Locale.ROOT),
        modifier = Modifier.padding(
            start = 8.dp,
            end = 8.dp,
            bottom = 2.dp
        ),
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        fontFamily = montserratFontFamily,
        color = MaterialTheme.colorScheme.onSecondaryContainer
    )
}

@Composable
fun Attachments(
    attachments: List<PostAttachment>,
    postEvent: (PostEvent) -> Unit
) {
    val images = attachments.filter {
        it.type in listOf(
            MimeType.JPEG.readableType,
            MimeType.PNG.readableType,
            MimeType.GIF.readableType,
            MimeType.BMP.readableType,
            MimeType.WEBP.readableType
        )
    }
    if (images.isNotEmpty()) {
        ImagePagerZ(
            pageCount = images.size,
            images = images,
            onImageClicked = { selectedImage ->
                postEvent(
                    PostEvent.OnImageClicked(
                        selectedImage
                    )
                )
            },
        )
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
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserImage(imageUrl)
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserName(
                userName = userName,
            )
            PostDate(
                publishedAt = publishedAt,

                )
        }
        Spacer(modifier = Modifier.weight(1f))
        OptionButton(
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
            onClick = { expanded = !expanded },
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 2.dp
                )
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "More Options",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
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
fun PostDate(
    publishedAt: String,
) {
    Text(
        text = DateUtils.calculateTimeDifference(publishedAt),
        modifier = Modifier
            .padding(
                start = 4.dp,
                end = 8.dp,
            ),
        color = Color.Gray,
        fontFamily = fontFamily2,
        fontSize = 12.sp
    )
}

@Composable
fun UserName(
    userName: String,
) {
    Text(
        text = userName.capitalize(Locale.ROOT),
        modifier = Modifier
            .padding(
                start = 4.dp,
                end = 8.dp,
            ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        softWrap = true,
        style = TextStyle(
            fontFamily = fontFamily,
        ),
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
fun UserImage(
    imageLink: String,
    size : Dp = 36.dp
) {
    SubcomposeAsyncImage(
        model = imageLink,
        contentDescription = "User Image",
        modifier = Modifier
            .padding(
                start = 8.dp,
                end = 8.dp,
            )
            .size(size)
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

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
//    color: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
    thickness: Dp = 1.dp
) {
    Box(
        modifier
            .fillMaxHeight()
            .width(thickness)
//            .background(color = color)
    )
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 33)
@Composable
fun FeedPostPreview() {
    AppTheme {
        Surface {
            FeedPostScreen(
                posts = listOf(
                    Post(
                        title = "Title",
                        body = "Body",
                        userName = "user Name",
                        publishedAt = "Sun Feb 04 16:47:40 GMT+02:00 2024",
                        authorEmail = "authorEmail",
                        attachments = listOf(
                            PostAttachment(
                                url = "https://www.google.com",
                                type = MimeType.PDF.readableType,
                                name = "PDF"
                            )
                        ),
                        tags = listOf(
                            Tag(
                                "Tag",
                                "#FF0000"
                            ),
                            Tag(
                                "Tagf",
                                "#FF0B00"
                            ),
                            Tag(
                                "Tag5",
                                "#FF0000"
                            ),
                        )
                    ),
                    Post(
                        title = "Title",
                        body = "Body",
                        userName = "user Name",
                        publishedAt = "Sun Feb 04 16:47:40 GMT+02:00 2024",
                        authorEmail = "authorEmail",
                    ),
                    Post(
                        title = "Title",
                        body = "Body",
                        userName = "user Name",
                        publishedAt = "Sun Feb 04 16:47:40 GMT+02:00 2024",
                        authorEmail = "authorEmail",
                    ),

                    ),
                postEvent = { PostEvent.Initial },
                currentEmail = "currentEmail",
            )
        }
    }

//    FeedPostItem(
//        post = Post(
//            title = "Title",
//            body = "Body",
//            userName = "user Name",
//            publishedAt = "Sun Feb 04 16:47:40 GMT+02:00 2024",
//            authorEmail = "authorEmail",
//        ),
//        postEvent = { PostEvent.Initial },
//        currentEmail = "currentEmail",
//    )


}

@Preview(
    showBackground = true, showSystemUi = true, apiLevel = 33,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun FeedPostPreviewNight() {
    AppTheme {
        Surface {
            FeedPostScreen(
                posts = listOf(
                    Post(
                        title = "Title",
                        body = "Body",
                        userName = "user Name",
                        publishedAt = "Sun Feb 04 16:47:40 GMT+02:00 2024",
                        authorEmail = "authorEmail",
                        attachments = listOf(
                            PostAttachment(
                                url = "https://www.google.com",
                                type = MimeType.PDF.readableType,
                                name = "PDF"
                            )
                        ),
                        tags = listOf(
                            Tag(
                                "Tag",
                                "#FF0000"
                            ),
                            Tag(
                                "Tagf",
                                "#FF0B00"
                            ),
                            Tag(
                                "Tag5",
                                "#FF0000"
                            ),
                        )
                    ),
                    Post(
                        title = "Title",
                        body = "Body",
                        userName = "user Name",
                        publishedAt = "Sun Feb 04 16:47:40 GMT+02:00 2024",
                        authorEmail = "authorEmail",
                    ),
                    Post(
                        title = "Title",
                        body = "Body",
                        userName = "user Name",
                        publishedAt = "Sun Feb 04 16:47:40 GMT+02:00 2024",
                        authorEmail = "authorEmail",
                    ),

                    ),
                postEvent = { PostEvent.Initial },
                currentEmail = "currentEmail",
            )
        }
    }

//    FeedPostItem(
//        post = Post(
//            title = "Title",
//            body = "Body",
//            userName = "user Name",
//            publishedAt = "Sun Feb 04 16:47:40 GMT+02:00 2024",
//            authorEmail = "authorEmail",
//        ),
//        postEvent = { PostEvent.Initial },
//        currentEmail = "currentEmail",
//    )


}
