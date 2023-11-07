package com.gp.chat.repository

import android.util.Log
import com.gp.chat.model.ChatGroup
import com.gp.chat.model.ChatUser
import com.gp.chat.model.Message
import com.gp.chat.model.NetworkMessage
import com.gp.chat.model.PrivateChats
import com.gp.chat.model.PrivateChatsNetwork
import com.gp.chat.model.RecentChat
import com.gp.chat.source.remote.MessageRemoteDataSource
import com.gp.socialapp.utils.State
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val messageRemoteDataSource: MessageRemoteDataSource
) : MessageRepository {
    val TAG = "MessageRepositoryImpl"
    override fun insertChat(chat: ChatGroup): Flow<State<String>> =
        messageRemoteDataSource.insertChat(chat)

    override fun insertRecentChat(recentChat: RecentChat, chatId: String): Flow<State<String>> =
        messageRemoteDataSource.insertRecentChat(recentChat, chatId)

    override fun sendMessage(message: Message): Flow<State<String>> =
        messageRemoteDataSource.sendMessage(message)

    override fun getMessages(chatId: String): Flow<State<List<Message>>> =
        messageRemoteDataSource.getMessages(chatId)

    override fun getRecentChats(chatId: List<String>): Flow<State<List<RecentChat>>> =
        messageRemoteDataSource.getRecentChats(chatId)

    override fun insertChatToUser(chatId: String, userEmail: String,receiverEmail:String): Flow<State<String>> =
        messageRemoteDataSource.insertChatToUser(chatId, userEmail,receiverEmail)


    override fun getUserChats(userEmail: String): Flow<State<ChatUser>> =
        messageRemoteDataSource.getUserChats(userEmail)

    override fun insertPrivateChat(sender: String, receiver: String, chatId: String): Flow<State<String>> =
        messageRemoteDataSource.insertPrivateChat(sender, receiver, chatId)

    override fun haveChatWithUser(userEmail: String, otherUserEmail: String): Flow<State<String>> =
        messageRemoteDataSource.haveChatWithUser(userEmail, otherUserEmail)

    override fun updateRecentChat(recentChat: RecentChat, chatId: String): Flow<State<String>> =
        messageRemoteDataSource.updateRecentChat(recentChat, chatId)



}