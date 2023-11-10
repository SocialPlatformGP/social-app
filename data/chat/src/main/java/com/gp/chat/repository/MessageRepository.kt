package com.gp.chat.repository


import com.gp.chat.model.ChatGroup
import com.gp.chat.model.ChatUser
import com.gp.chat.model.Message
import com.gp.chat.model.RecentChat
import com.gp.socialapp.utils.State
import com.gp.chat.model.NetworkMessage
import com.gp.chat.model.PrivateChats
import com.gp.chat.model.PrivateChatsNetwork
import com.gp.chat.model.RecentChat
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun sendMessage(chatId: String, message: Message)
    fun getChatMessages(chatId: String): Flow<List<Message>>
    fun fetchGroupChatMessages(groupId: String): Flow<List<Message>>
    fun sendGroupMessage(message: Message, recentChat: RecentChat): Flow<State<Nothing>>
    fun insertChat(chat:ChatGroup): Flow<State<String>>
    fun insertRecentChat(recentChat: RecentChat,chatId: String): Flow<State<String>>
    fun sendMessage(message: Message): Flow<State<String>>
    fun getMessages(chatId: String): Flow<State<List<Message>>>
    fun getRecentChats(chatId: List<String>): Flow<State<List<RecentChat>>>
    fun insertChatToUser(chatId:String,userEmail: String,receiverEmail:String): Flow<State<String>>
    fun getUserChats(userEmail: String): Flow<State<ChatUser>>
    fun insertPrivateChat(sender:String,receiver:String,chatId: String): Flow<State<String>>
    fun haveChatWithUser(userEmail: String, otherUserEmail: String): Flow<State<String>>
    fun updateRecentChat(recentChat: RecentChat, chatId: String): Flow<State<String>>
}