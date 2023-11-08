package com.gp.chat.repository

import android.util.Log
import com.gp.chat.model.Message
import com.gp.chat.model.NetworkChatGroup
import com.gp.chat.model.NetworkRecentChat
import com.gp.chat.model.RecentChat
import com.gp.chat.source.remote.MessageRemoteDataSource
import com.gp.chat.source.remote.RemoveSpecialChar
import com.gp.chat.util.DateUtils.getTimeStamp
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val messageRemoteDataSource: MessageRemoteDataSource
): MessageRepository {
    override suspend fun sendMessage(chatId: String, message: Message) {
        Log.d("viewmodel->repo", "sendMessage: $message")
        return messageRemoteDataSource.sendMessage(chatId, message)
    }

    override fun getChatMessages(chatId: String): Flow<List<Message>> {

        return messageRemoteDataSource.getChatMessages(chatId)
    }

    override fun fetchGroupChatMessages(groupId: String): Flow<List<Message>> {
        return messageRemoteDataSource.fetchGroupMessages(groupId)
    }

    override fun sendGroupMessage(message: Message, recentChat: RecentChat): Flow<State<Nothing>> {
        return messageRemoteDataSource.sendGroupMessage(message, recentChat)
    }

    override fun createGroupChat(
        name: String,
        avatarLink: String,
        members: List<String>
    ): Flow<State<String>> {
        Log.d("viewmodel->repo", "createGroupChat: $name")
        val group = NetworkChatGroup(name = name,
            members.map{RemoveSpecialChar.removeSpecialCharacters(it)}.associateWith { true })
        val recentChat = NetworkRecentChat(
            lastMessage = "\t",
            timestamp = getTimeStamp(Date()),
            title = name,
            senderName = "N/A",
            receiverName = "N/A",
            isPrivateChat = false,
            picUrl = avatarLink
        )
        return messageRemoteDataSource.createGroupChat(group, recentChat)
    }

}