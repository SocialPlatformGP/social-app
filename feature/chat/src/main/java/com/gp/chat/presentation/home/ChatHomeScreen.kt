package com.gp.chat.presentation.home

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseUser
import com.gp.chat.R
import com.gp.chat.listener.OnRecentChatClicked
import com.gp.chat.model.RecentChat
import kotlinx.coroutines.Dispatchers
import okhttp3.Dispatcher
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatHomeScreen(
    viewModel: HomeViewModel,
    onRecentChatClicked: (RecentChat) -> Unit,
    onRecentChatLongClicked: (RecentChat) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chats by viewModel.recentChats.collectAsStateWithLifecycle()
    val state by viewModel.chatHomeState.collectAsStateWithLifecycle()
    Scaffold(topBar = { } ,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFabClick
            ) {
                Icon(Icons.Filled.Add,"")
            }
        }
        , content = {
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .padding(it)){
                items(chats){chat ->
                    ChatItem(chat, state.currentUser, onRecentChatClicked, onRecentChatLongClicked, modifier)
                }
            }
        })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatItem(
    chatItem: RecentChat,
    currentUser: FirebaseUser,
    onChatClicked: (RecentChat) -> Unit,
    onChatLongClicked: (RecentChat) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageURL = when{
        chatItem.privateChat && chatItem.senderName == currentUser.displayName -> {
            chatItem.receiverPicUrl
        }
        else -> {
            chatItem.senderPicUrl
        }
    }
    val name = when{
        chatItem.privateChat && chatItem.senderName == currentUser.displayName -> {
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
    val timestamp = DateTimeFormatter
        .ofPattern("hh:mm", Locale.ENGLISH)
        .format(ZonedDateTime.parse(chatItem.timestamp,formatter))
    Column (
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ){
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            onChatLongClicked(chatItem)
                        },
                        onTap = {
                            onChatClicked(chatItem)
                        }
                    )
                }){
            HomeChatIcon(imageURL, modifier)
            Spacer(Modifier.width(12.dp))
            Column (
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = modifier
            ){
                Text(
                    text = name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = chatItem.lastMessage,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
                Text(
                    text = timestamp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }
        }
        Divider(thickness = 2.dp,
            modifier = modifier.padding(top = 8.dp))
    }
}
@Composable
fun HomeChatIcon(
    imageURL: String,
    modifier: Modifier = Modifier
){
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(imageURL)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(imageURL)
        .diskCacheKey(imageURL)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()
    AsyncImage(
        model = imageRequest,
        contentDescription = "Image Description",
        contentScale = ContentScale.FillBounds,
        modifier = modifier
            .size(70.dp)
            .clip(CircleShape),
    )
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Preview
//@Composable
//fun HomeChatItemPreview() {
//    MaterialTheme {
//        ChatItem(chatItem = RecentChat(), {})
//    }
//}