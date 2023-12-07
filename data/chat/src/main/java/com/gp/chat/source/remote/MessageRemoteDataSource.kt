package com.gp.chat.source.remote

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.gp.chat.model.ChatGroup
import com.gp.chat.model.ChatUser
import com.gp.chat.model.Message
import com.gp.chat.model.NetworkMessage
import com.gp.chat.model.NetworkRecentChat
import com.gp.chat.model.PrivateChats
import com.gp.chat.model.PrivateChatsNetwork
import com.gp.chat.model.NetworkChatGroup
import com.gp.chat.model.RecentChat
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow

interface MessageRemoteDataSource {
    fun fetchGroupMessages(groupId: String): Flow<List<Message>>
    fun sendGroupMessage(message: Message): Flow<State<Nothing>>
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
    fun deleteMessage(messageId: String,chatId: String)
    fun updateMessage(messageId: String,chatId: String, updatedText: String)
    fun leaveGroup(chatId: String)

    fun createGroupChat(group: NetworkChatGroup, recentChat: NetworkRecentChat): Flow<State<String>>
    fun getGroupMembersEmails(groupId: String): Flow<State<List<String>>>
    fun removeMemberFromGroup(groupId: String, memberEmail: String): Flow<State<String>>
//    fun sendMessageWithFile(message: Message, fileUri: Uri, chatId: String)
}