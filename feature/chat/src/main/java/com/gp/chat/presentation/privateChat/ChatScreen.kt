package com.gp.chat.presentation.privateChat

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.gp.chat.R
import com.gp.chat.model.Message
import com.gp.chat.presentation.groupchat.GroupChatViewModel
import com.gp.chat.presentation.home.DropDownItem
import com.gp.chat.util.DateUtils.getDateHeader
import com.gp.chat.utils.CircularAvatar
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    viewModel: PrivateChatViewModel,
    isPrivateChat: Boolean,
    chatTitle: String,
    chatImageURL: String,
    onChatHeaderClicked: () -> Unit,
    onBackPressed: () -> Unit,
    onFileClicked: (String, String, String) -> Unit,
    onImageClicked: (String) -> Unit,
    onUserClicked: (String) -> Unit,
    onAttachFileClicked: () -> Unit,
    onAttachImageClicked: () -> Unit,
    onOpenCameraClicked: () -> Unit,
    dropDownItems: List<DropDownItem>,
    onDropPDownItemClicked: (DropDownItem, String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val currentMessage by viewModel.currentMessage.collectAsStateWithLifecycle()
    ChatScreen(
        isPrivateChat = isPrivateChat,
        chatTitle = chatTitle,
        chatImageURL = chatImageURL,
        onChatHeaderClicked = onChatHeaderClicked,
        onBackPressed = onBackPressed,
        onFileClicked = onFileClicked,
        onImageClicked = onImageClicked,
        onUserClicked = onUserClicked,
        onAttachFileClicked = onAttachFileClicked,
        onAttachImageClicked = onAttachImageClicked,
        onOpenCameraClicked = onOpenCameraClicked,
        dropDownItems = dropDownItems,
        onDropPDownItemClicked = onDropPDownItemClicked,
        onSendMessage = viewModel::sendMessage,
        onUpdateMessage = viewModel::setCurrentMessage,
        messages = messages,
        currentMessage = currentMessage,
        currentUserEmail = viewModel.currentUser.email!!,
        modifier = modifier
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    viewModel: GroupChatViewModel,
    isPrivateChat: Boolean,
    chatTitle: String,
    chatImageURL: String,
    onChatHeaderClicked: () -> Unit,
    onBackPressed: () -> Unit,
    onFileClicked: (String, String, String) -> Unit,
    onImageClicked: (String) -> Unit,
    onUserClicked: (String) -> Unit,
    onAttachFileClicked: () -> Unit,
    onAttachImageClicked: () -> Unit,
    onOpenCameraClicked: () -> Unit,
    dropDownItems: List<DropDownItem>,
    onDropPDownItemClicked: (DropDownItem, String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val currentMessage by viewModel.currentMessageState.collectAsStateWithLifecycle()
    ChatScreen(
        isPrivateChat = isPrivateChat,
        chatTitle = chatTitle,
        chatImageURL = chatImageURL,
        onChatHeaderClicked = onChatHeaderClicked,
        onBackPressed = onBackPressed,
        onFileClicked = onFileClicked,
        onImageClicked = onImageClicked,
        onUserClicked = onUserClicked,
        onAttachFileClicked = onAttachFileClicked,
        onAttachImageClicked = onAttachImageClicked,
        onOpenCameraClicked = onOpenCameraClicked,
        dropDownItems = dropDownItems,
        onDropPDownItemClicked = onDropPDownItemClicked,
        onSendMessage = viewModel::onSendMessage,
        onUpdateMessage = viewModel::updateCurrentMessage,
        messages = messages,
        currentMessage = currentMessage,
        currentUserEmail = viewModel.currentUser.email!!,
        modifier = modifier,
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    isPrivateChat: Boolean,
    chatTitle: String,
    chatImageURL: String,
    onChatHeaderClicked: () -> Unit,
    onBackPressed: () -> Unit,
    onFileClicked: (String, String, String) -> Unit,
    onImageClicked: (String) -> Unit,
    onUserClicked: (String) -> Unit,
    onAttachFileClicked: () -> Unit,
    onAttachImageClicked: () -> Unit,
    onOpenCameraClicked: () -> Unit,
    dropDownItems: List<DropDownItem>,
    onDropPDownItemClicked: (DropDownItem, String, String) -> Unit,
    onSendMessage: () -> Unit,
    onUpdateMessage: (String) -> Unit,
    messages: List<Message>,
    currentMessage: MessageState,
    currentUserEmail: String,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val maxScreenWidthDP = configuration.screenWidthDp.dp
    val maxScreenHeightDP = configuration.screenHeightDp.dp
    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconButton(onClick = { onBackPressed() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "Navigate Back"
                    )
                }
                Row(
                    modifier = Modifier
                        .clickable { onChatHeaderClicked() }
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.size(8.dp))
                    if (chatImageURL.isNotBlank()) {
                        CircularAvatar(imageURL = chatImageURL, size = 45.dp)
                    } else {
                        Image(
                            painter = painterResource(
                                id = if (isPrivateChat)
                                    R.drawable.baseline_account_circle_24
                                else
                                    R.drawable.ic_group
                            ),
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = chatTitle,
                        fontSize = 18.sp
                    )
                }
            }
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
        modifier = modifier
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MessagesContent(
                messages = messages,
                currentUserEmail = currentUserEmail,
                isPrivateChat = isPrivateChat,
                onFileClicked = onFileClicked,
                onImageClicked = onImageClicked,
                onUserClicked = onUserClicked,
                modifier = Modifier.weight(1f),
                scrollState = scrollState,
                dropDownItems = dropDownItems,
                onDropPDownItemClicked = onDropPDownItemClicked,
                maxScreenWidthDP = maxScreenWidthDP,
                maxScreenHeightDP = maxScreenHeightDP,
            )
            MessageInput(
                onAttachFileClicked = onAttachFileClicked,
                onAttachImageClicked = onAttachImageClicked,
                onOpenCameraClicked = onOpenCameraClicked,
                onSendMessage = { onSendMessage() },
                onUpdateMessage = onUpdateMessage,
                messageState = currentMessage,
            )
        }
    }
}

@Composable
fun MessageInput(
    onAttachFileClicked: () -> Unit,
    onAttachImageClicked: () -> Unit,
    onOpenCameraClicked: () -> Unit,
    onSendMessage: () -> Unit,
    onUpdateMessage: (String) -> Unit,
    messageState: MessageState,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val stateTransition: Transition<Boolean> =
        updateTransition(targetState = isExpanded, label = "")
    val rotation: Float by stateTransition.animateFloat(
        transitionSpec = {
            if (isExpanded) {
                spring(stiffness = Spring.StiffnessLow)
            } else {
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = ""
    ) {
        if (it) -45f else 0f
    }
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Divider(
            modifier = Modifier
                .padding(bottom = 2.dp)
                .size(2.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageState.message,
                onValueChange = { value -> onUpdateMessage(value) },
                placeholder = { Text("Type your message") },
                modifier = Modifier
                    .weight(1F)
                    .clip(RoundedCornerShape(35.dp)),
                leadingIcon = {
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                }
            )
            IconButton(onClick = { onSendMessage() }) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        if (isExpanded) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onAttachFileClicked() }) {
                    Icon(
                        painterResource(id = R.drawable.baseline_attach_file_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { onAttachImageClicked() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_image_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { onOpenCameraClicked() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessagesContent(
    modifier: Modifier = Modifier,
    scrollState: LazyListState,
    messages: List<Message>,
    currentUserEmail: String,
    isPrivateChat: Boolean,
    onFileClicked: (String, String, String) -> Unit,
    onImageClicked: (String) -> Unit,
    onUserClicked: (String) -> Unit,
    dropDownItems: List<DropDownItem>,
    onDropPDownItemClicked: (DropDownItem, String, String) -> Unit,
    maxScreenWidthDP: Dp,
    maxScreenHeightDP: Dp,
) {
    val reversedMessages = messages.asReversed()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        reverseLayout = true,
        state = scrollState,
    ) {
        for ((index, message) in reversedMessages.withIndex()) {
            val previousMessage = reversedMessages.getOrNull(index + 1)
            val isSameSender = previousMessage?.senderId.equals(message.senderId)
            val isCurrentUser = message.senderId == currentUserEmail
            Log.d("seerde", "messageMail: ${message.senderId}, currentMail: $currentUserEmail")
            item {
                MessageItem(
                    message = message,
                    onFileClicked = onFileClicked,
                    onImageClicked = onImageClicked,
                    onUserClicked = onUserClicked,
                    isPrivateChat = isPrivateChat,
                    isSameSender = isSameSender,
                    isCurrentUser = isCurrentUser,
                    dropDownItems = dropDownItems,
                    onDropPDownItemClicked = onDropPDownItemClicked,
                    maxScreenWidthDP = maxScreenWidthDP,
                    maxScreenHeightDP = maxScreenHeightDP,
                )
            }
            if (previousMessage?.messageDate != message.messageDate) {
                item {
                    DateHeader(
                        dateString = message.messageDate,
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateHeader(
    modifier: Modifier = Modifier,
    dateString: String,
) {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
    val localDate: LocalDate = LocalDate.parse(dateString, formatter)
    val header = getDateHeader(localDate)
    Row(modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
        Text(
            text = header,
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Divider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    message: Message,
    onFileClicked: (String, String, String) -> Unit,
    onImageClicked: (String) -> Unit,
    onUserClicked: (String) -> Unit,
    dropDownItems: List<DropDownItem>,
    onDropPDownItemClicked: (DropDownItem, String, String) -> Unit,
    isPrivateChat: Boolean,
    isSameSender: Boolean,
    isCurrentUser: Boolean,
    maxScreenWidthDP: Dp,
    maxScreenHeightDP: Dp,
) {
    val topPadding = if (isSameSender) 2.dp else 12.dp
    val extendedFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
    val shortFormat = DateTimeFormatter.ofPattern("HH:mm")
    val dateTime = LocalDateTime.parse(message.timestamp, extendedFormat)
    val timestamp = shortFormat.format(dateTime)
    val horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    val backgroundColor =
        if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val surfaceShape = if (isCurrentUser) {
        RoundedCornerShape(20.dp, if (!isSameSender) 5.dp else 20.dp, 20.dp, 20.dp)
    } else {
        RoundedCornerShape(if (!isSameSender) 5.dp else 20.dp, 20.dp, 20.dp, 20.dp)
    }
    var isDropDownMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHieght by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    Row(
        modifier = modifier
            .padding(
                top = topPadding,
                start = if (isPrivateChat) 16.dp else 0.dp,
                end = if (isPrivateChat) 16.dp else 0.dp
            )
            .fillMaxWidth(),
        horizontalArrangement = horizontalArrangement
    ) {
        if (!isCurrentUser && !isSameSender && message.senderPfpURL.isNotBlank() && !isPrivateChat) {
            CircularAvatar(
                imageURL = message.senderPfpURL,
                size = 42.dp,
                onClick = { onUserClicked(message.senderId) },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Top)
            )
        } else if (!isCurrentUser && !isSameSender && !isPrivateChat) {
            Image(
                painter = painterResource(id = R.drawable.baseline_account_circle_24),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable { onUserClicked(message.senderId) })
        } else if (!isCurrentUser && message.senderPfpURL.isNotBlank() && !isPrivateChat) {
            Spacer(modifier = Modifier.width(74.dp))
        }
        Surface(
            color = backgroundColor,
            shape = surfaceShape,
            modifier = modifier
                .widthIn(max = maxScreenWidthDP * 0.67f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            if (isCurrentUser) {
                                isDropDownMenuVisible = true
                                pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                            }
                        }
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .onSizeChanged {
                        itemHieght = with(density) { it.height.toDp() }
                    }
            ) {
                if (!isCurrentUser && !isSameSender && !isPrivateChat) {
                    ClickableText(
                        text = AnnotatedString(message.senderName),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        onClick = { onUserClicked(message.senderId) })
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (message.fileType.contains("image") && message.message.isBlank()) {
                    val imageURL = message.fileURI.toString()
                    ImageMessageWithTimestamp(
                        imageURL = imageURL,
                        onImageClicked = { onImageClicked(imageURL) },
                        maxHeight = maxScreenHeightDP * 0.4f,
                        timestamp = timestamp
                    )
                } else {
                    if (message.fileURI.toString().isNotBlank()) {
                        MessageAttachment(
                            fileURI = message.fileURI,
                            fileType = message.fileType,
                            fileName = message.fileNames,
                            onFileClicked = {
                                onFileClicked(
                                    message.fileURI.toString(),
                                    message.fileType,
                                    message.fileNames
                                )
                            },
                            onImageClicked = { onImageClicked(message.fileURI.toString()) },
                            maxHeight = maxScreenHeightDP * 0.4f
                        )
                    }
                    if (message.message.isNotBlank()) {
                        Text(
                            text = message.message,
                            fontWeight = FontWeight.Light,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        )
                    }
                    Text(
                        text = timestamp,
                        fontWeight = FontWeight.W400,
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 4.dp, bottom = 4.dp)
                    )
                }
                DropdownMenu(
                    expanded = isDropDownMenuVisible,
                    onDismissRequest = {
                        isDropDownMenuVisible = false
                    },
                    offset = pressOffset.copy(
                        y = pressOffset.y - itemHieght
                    )
                ) {
                    dropDownItems.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text(text = item.text)
                            },
                            onClick = {
                                isDropDownMenuVisible = false
                                onDropPDownItemClicked(item, message.id, message.message)
                            })
                    }

                }
            }
        }
    }
}

@Composable
fun ImageMessageWithTimestamp(
    modifier: Modifier = Modifier,
    imageURL: String,
    onImageClicked: () -> Unit,
    maxHeight: Dp,
    timestamp: String,
) {
    val density = LocalDensity.current.density
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Box {
            MessageImageAttachment(
                imageURL = imageURL,
                onImageClicked = { onImageClicked() },
                maxHeight = maxHeight,
                modifier = Modifier.onGloballyPositioned {
                    width = it.size.width / density
                    height = it.size.height / density
                }
            )
            Column(
                Modifier
                    .size(width.dp, height.dp)
                    .background(
                        Brush.linearGradient(
                            0.875F to Color.Transparent,
                            1F to Color.DarkGray,
                            start = Offset.Zero,
                            end = Offset.Infinite
                        )
                    )
            ) {}
            Text(
                text = timestamp,
                fontWeight = FontWeight.W400,
                fontSize = 10.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 8.dp),
                color = Color.White,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun MessageAttachment(
    modifier: Modifier = Modifier,
    fileURI: Uri,
    fileType: String,
    fileName: String,
    maxHeight: Dp,
    onFileClicked: () -> Unit = {},
    onImageClicked: () -> Unit = {},
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
    ) {
        when {
            fileType.contains("image") -> {
                MessageImageAttachment(
                    imageURL = fileURI.toString(),
                    onImageClicked = { onImageClicked() },
                    maxHeight = maxHeight,
                    modifier = modifier,
                )
            }

            else -> {
                MessageFileAttachment(
                    fileType = fileType,
                    fileName = fileName,
                    onFileClicked = { onFileClicked() },
                    modifier = modifier,
                )
            }
        }
    }
}

@Composable
fun MessageImageAttachment(
    imageURL: String,
    onImageClicked: () -> Unit,
    maxHeight: Dp,
    modifier: Modifier = Modifier,
) {
    val imageRequest =
        ImageRequest.Builder(LocalContext.current).data(imageURL)
            .dispatcher(Dispatchers.IO)
            .memoryCacheKey(imageURL).diskCacheKey(imageURL).diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED).build()
    AsyncImage(
        model = imageRequest,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight)
            .clickable { onImageClicked() },
    )
}

@Composable
fun getFilePainterResource(fileType: String): Painter {
    return painterResource(
        id = when {
            fileType.contains("application/pdf") -> R.drawable.ic_pdf
            fileType.contains("audio") -> R.drawable.ic_audio
            fileType.contains("video") -> R.drawable.ic_video
            fileType.contains("text") -> R.drawable.ic_text
            fileType.contains("wordprocessingml") || fileType.contains("msword") -> R.drawable.ic_word
            fileType.contains("powerpoint") || fileType.contains("presentation") -> R.drawable.ic_ppt
            fileType.contains("excel") || fileType.contains("spreadsheet") -> R.drawable.ic_excel
            else -> R.drawable.ic_file
        }
    )
}

@Composable
fun MessageFileAttachment(
    fileType: String,
    fileName: String,
    onFileClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Surface(
        color = Color(0f, 0f, 0f, 0.15f),
        shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clickable { onFileClicked() }
                .padding(8.dp)) {
            Icon(
                painter = getFilePainterResource(fileType),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = fileName,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}