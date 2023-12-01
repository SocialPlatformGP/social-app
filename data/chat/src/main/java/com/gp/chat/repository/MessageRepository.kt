package com.gp.chat.repository


import com.gp.chat.model.ChatGroup
import com.gp.chat.model.ChatUser
import com.gp.chat.model.Message
import com.gp.chat.model.RecentChat
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    fun fetchGroupChatMessages(groupId: String): Flow<List<Message>>
    fun sendGroupMessage(message: Message, recentChat: RecentChat): Flow<State<Nothing>>
    fun createGroupChat(name: String, avatarLink: String, members: List<String>, currentUserEmail: String): Flow<State<String>>
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
    fun getGroupMembersEmails(groupId: String): Flow<State<List<String>>>
    fun removeMemberFromGroup(groupId: String, memberEmail: String): Flow<State<String>>
}