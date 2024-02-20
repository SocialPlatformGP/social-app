package com.gp.chat.presentation.home

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseUser
import com.gp.chat.R
import com.gp.chat.model.RecentChat
import com.gp.chat.presentation.theme.AppTheme
import com.gp.chat.util.DateUtils
import com.gp.chat.utils.CircularAvatar
import com.gp.chat.utils.FabItem
import com.gp.chat.utils.MultiFloatingActionButton
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatHomeScreen(
    viewModel: HomeViewModel,
    onRecentChatClicked: (RecentChat) -> Unit,
    dropDownItems: List<DropDownItem>,
    onDropPDownItemClicked: (DropDownItem, RecentChat) -> Unit,
    fabItems: ArrayList<FabItem>,
    modifier: Modifier = Modifier
) {
    val chats by viewModel.recentChats.collectAsStateWithLifecycle()
    val state by viewModel.chatHomeState.collectAsStateWithLifecycle()
    ChatHomeScreen(
        chats = chats,
        state = state,
        onRecentChatClicked = onRecentChatClicked,
        dropDownItems = dropDownItems,
        onDropPDownItemClicked = onDropPDownItemClicked,
        fabItems = fabItems,
        modifier = modifier
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatHomeScreen(
    chats: List<RecentChat>,
    state: ChatHomeState?,
    onRecentChatClicked: (RecentChat) -> Unit,
    dropDownItems: List<DropDownItem>,
    onDropPDownItemClicked: (DropDownItem, RecentChat) -> Unit,
    fabItems: ArrayList<FabItem>,
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = {
        ChatTopBar()
    }, floatingActionButton = {
        MultiFloatingActionButton(
            fabIcon = Icons.Filled.Add,
            items = fabItems,
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    }, content = {
        ChatHomeScreenContent(
            chats = chats,
            state = state,
            onRecentChatClicked = onRecentChatClicked,
            dropDownItems = dropDownItems,
            onDropPDownItemClicked = onDropPDownItemClicked,
            modifier.padding(it)
        )
    },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    )
}

@Composable
fun ChatTopBar() {
    Row(

        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(
                RoundedCornerShape(
                    bottomEnd = 16.dp,
                    bottomStart = 16.dp
                )
            )
            .background(
                androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,


    ) {
        IconButton(onClick = {
            //todo navigate to search
        }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "search",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = "EduLink",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(align = Alignment.CenterHorizontally)
        )
        IconButton(onClick = {
            //todo navigate to notifications
        }) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = "notification",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatHomeScreenContent(
    chats: List<RecentChat>,
    state: ChatHomeState?,
    onRecentChatClicked: (RecentChat) -> Unit,
    dropDownItems: List<DropDownItem>,
    onDropPDownItemClicked: (DropDownItem, RecentChat) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(
            vertical = 8.dp, horizontal = 16.dp
        ), modifier = modifier.fillMaxWidth()
    ) {
        items(chats) { chat ->
            ChatItem(
                chat, state?.currentUser, onRecentChatClicked, dropDownItems, onDropPDownItemClicked
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatItem(
    chatItem: RecentChat,
    currentUser: FirebaseUser?,
    onChatClicked: (RecentChat) -> Unit,
    dropDownItems: List<DropDownItem>,
    onItemClicked: (DropDownItem, RecentChat) -> Unit,
    modifier: Modifier = Modifier
) {
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
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val imageURL = when {
        chatItem.privateChat && chatItem.senderPicUrl == currentUser?.photoUrl.toString() -> {
            chatItem.receiverPicUrl
        }

        else -> {
            chatItem.senderPicUrl
        }
    }
    val name = when {
        chatItem.privateChat && chatItem.senderPicUrl == currentUser?.photoUrl.toString() -> {
            chatItem.receiverName
        }

        chatItem.privateChat -> {
            chatItem.senderName
        }

        else -> {
            chatItem.title
        }
    }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH)
    val timestamp = DateUtils.getTimeStamp(ZonedDateTime.parse(chatItem.timestamp, formatter))
    Column(modifier = modifier
        .padding(start = 8.dp, end = 8.dp, top = 16.dp)
        .fillMaxWidth()
        .onSizeChanged {
            itemHieght = with(density) { it.height.toDp() }
        }
        .indication(interactionSource = interactionSource, LocalIndication.current)
        .pointerInput(Unit) {
            detectTapGestures(onLongPress = {
                if (!chatItem.privateChat) {
                    isDropDownMenuVisible = true
                    pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                }
            }, onTap = {
                onChatClicked(chatItem)
            }, onPress = {
                val press = PressInteraction.Press(it)
                interactionSource.emit(press)
                tryAwaitRelease()
                interactionSource.emit(PressInteraction.Release(press))
            })
        }) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth()
        ) {
            if (chatItem.privateChat) {
                CircularAvatar(
                    imageURL, 55.dp, placeHolderImageVector = Icons.Filled.AccountCircle
                )
            } else {
                CircularAvatar(
                    imageURL = imageURL, size = 55.dp, placeHolderDrawable = R.drawable.ic_group
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(
                horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 20.sp
                    )
                    Text(
                        text = timestamp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = chatItem.lastMessage,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }
        }
        Divider(
            thickness = 1.dp,
            modifier = modifier.padding(top = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        DropdownMenu(
            expanded = isDropDownMenuVisible, onDismissRequest = {
                isDropDownMenuVisible = false
            }, offset = pressOffset.copy(
                y = pressOffset.y - itemHieght
            )
        ) {
            dropDownItems.forEach { item ->
                DropdownMenuItem(text = {
                    Text(text = item.text)
                }, onClick = {
                    isDropDownMenuVisible = false
                    onItemClicked(item, chatItem)
                })
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Light", showBackground = true, showSystemUi = true)
@Preview(
    name = "Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ChatHomeScreenPreview() {
    val chats = listOf<RecentChat>(
        RecentChat(
            id = "sed",
            lastMessage = "leo",
            timestamp = "2024-02-16 20:38:52 GMT+02:00",
            title = "nisi",
            senderName = "Alton Russo",
            receiverName = "Debora Shannon",
            privateChat = false,
            senderPicUrl = "",
            receiverPicUrl = ""
        ), RecentChat(
            id = "sed",
            lastMessage = "leo",
            timestamp = "2024-02-15 20:38:52 GMT+02:00",
            title = "nisi",
            senderName = "Alton Russo",
            receiverName = "Debora Shannon",
            privateChat = true,
            senderPicUrl = "",
            receiverPicUrl = ""
        ), RecentChat(
            id = "sed",
            lastMessage = "leo",
            timestamp = "2024-02-12 20:38:52 GMT+02:00",
            title = "nisi",
            senderName = "Alton Russo",
            receiverName = "Debora Shannon",
            privateChat = false,
            senderPicUrl = "",
            receiverPicUrl = ""
        ), RecentChat(
            id = "sed",
            lastMessage = "leo",
            timestamp = "2024-02-02 20:38:52 GMT+02:00",
            title = "nisi",
            senderName = "Alton Russo",
            receiverName = "Debora Shannon",
            privateChat = false,
            senderPicUrl = "",
            receiverPicUrl = ""
        )
    )
    val dropDownItems = listOf<DropDownItem>()
    val fabItems = arrayListOf<FabItem>()
    AppTheme {
        ChatHomeScreen(
            chats = chats,
            state = null,
            onRecentChatClicked = {},
            dropDownItems = dropDownItems,
            onDropPDownItemClicked = { _, _ -> },
            fabItems = fabItems,
        )
    }
}

