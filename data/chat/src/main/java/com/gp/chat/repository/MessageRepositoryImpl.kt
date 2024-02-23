package com.gp.chat.repository

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseUser
import com.gp.chat.model.ChatGroup
import com.gp.chat.model.ChatUser
import com.gp.chat.model.Message
import com.gp.chat.model.NetworkChatGroup
import com.gp.chat.model.NetworkRecentChat
import com.gp.chat.model.RecentChat
import com.gp.chat.source.remote.MessageRemoteDataSource
import com.gp.chat.util.DateUtils.getTimeStamp
import com.gp.chat.util.RemoveSpecialChar
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val messageRemoteDataSource: MessageRemoteDataSource
) : MessageRepository {
    val TAG = "MessageRepositoryImpl"
    override fun insertChat(chat: ChatGroup): Flow<State<String>> =
        messageRemoteDataSource.insertChat(chat)

    override fun insertRecentChat(recentChat: RecentChat, chatId: String) =
        messageRemoteDataSource.insertRecentChat(recentChat, chatId)

    override fun sendMessage(message: Message): Flow<State<String>> =
        messageRemoteDataSource.sendMessage(message)

    override fun getMessages(chatId: String): Flow<State<List<Message>>> =
        messageRemoteDataSource.getMessages(chatId)

    override fun getRecentChats(chatId: List<String>): Flow<State<List<RecentChat>>> =
        messageRemoteDataSource.getRecentChats(chatId)

    override fun insertChatToUser(
        chatId: String,
        userEmail: String,
        receiverEmail: String
    ): Flow<State<String>> =
        messageRemoteDataSource.insertChatToUser(chatId, userEmail, receiverEmail)


    override fun fetchGroupChatMessages(groupId: String): Flow<List<Message>> {
        return messageRemoteDataSource.fetchGroupMessages(groupId)
    }



    override fun getUserChats(userEmail: String): Flow<State<ChatUser>> =
        messageRemoteDataSource.getUserChats(userEmail)

    override fun insertPrivateChat(
        sender: String,
        receiver: String,
        chatId: String
    ): Flow<State<String>> =
        messageRemoteDataSource.insertPrivateChat(sender, receiver, chatId)

    override fun haveChatWithUser(userEmail: String, otherUserEmail: String): Flow<State<String>> =
        messageRemoteDataSource.haveChatWithUser(userEmail, otherUserEmail)

    override fun updateRecentChat(recentChat: RecentChat, chatId: String): Flow<State<String>> =
        messageRemoteDataSource.updateRecentChat(recentChat, chatId)

    override fun deleteMessage(messageId: String, chatId: String) {
        messageRemoteDataSource.deleteMessage(messageId, chatId)
    }

    override fun updateMessage(messageId: String, chatId: String, updatedText: String) {
        messageRemoteDataSource.updateMessage(messageId, chatId, updatedText)
    }

    override fun leaveGroup(chatId: String) {
        messageRemoteDataSource.leaveGroup(chatId)
    }

    override fun getGroupDetails(groupId: String): Flow<State<ChatGroup>> {
        return messageRemoteDataSource.getGroupDetails(groupId)
    }

    override fun removeMemberFromGroup(groupId: String, memberEmail: String): Flow<State<String>> {
        return messageRemoteDataSource.removeMemberFromGroup(groupId, memberEmail)
    }

    override fun updateGroupAvatar(uri: Uri, oldURL: String, groupID: String)
        = messageRemoteDataSource.updateGroupAvatar(uri, oldURL, groupID)
    override fun addGroupMembers(groupId: String, usersEmails: List<String>): Flow<State<Nothing>> {
        return messageRemoteDataSource.addGroupMembers(groupId, usersEmails)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun createGroupChat(
        name: String,
        avatarLink: String,
        members: List<String>,
        currentUserEmail: String
    ): Flow<State<String>> {
        val currentTime: ZonedDateTime = now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH)
        val formatted = currentTime.format(formatter)
        Log.d("viewmodel->repo", "createGroupChat: $name")
        val membersMap = members.map { RemoveSpecialChar.removeSpecialCharacters(it) }
            .associateWith { false } + mapOf(
            RemoveSpecialChar.removeSpecialCharacters(
                currentUserEmail
            ) to true
        )
        val group = NetworkChatGroup(name, avatarLink, membersMap)
        val recentChat = NetworkRecentChat(
            lastMessage = "No messages yet",
            timestamp = formatted,
            title = name,
            senderName = "N/A",
            receiverName = "N/A",
            privateChat = false,
            senderPicUrl = avatarLink
        )
        return messageRemoteDataSource.createGroupChat(group, recentChat)
    }
    override fun changeGroupName(groupID: String, newName: String)
        = messageRemoteDataSource.changeGroupName(groupID, newName)
}