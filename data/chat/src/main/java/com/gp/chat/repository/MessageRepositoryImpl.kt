package com.gp.chat.repository

import android.util.Log
import com.gp.chat.model.GroupMessage
import com.gp.chat.model.Message
import com.gp.chat.source.remote.MessageRemoteDataSource
import kotlinx.coroutines.flow.Flow
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

    override fun fetchGroupChatMessages(groupId: String): Flow<List<GroupMessage>> {
        return messageRemoteDataSource.fetchGroupChatMessages(groupId)
    }
}