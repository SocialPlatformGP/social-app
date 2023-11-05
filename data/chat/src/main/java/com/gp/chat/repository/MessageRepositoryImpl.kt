package com.gp.chat.repository

import android.util.Log
import com.gp.chat.model.Message
import com.gp.chat.model.NetworkMessage
import com.gp.chat.source.remote.MessageRemoteDataSource
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val messageRemoteDataSource: MessageRemoteDataSource
): MessageRepository {
    override suspend fun sendMessage(chatId: String, message: NetworkMessage): String {
        Log.d("viewmodel->repo", "sendMessage: $message")
        return messageRemoteDataSource.sendMessage(chatId, message)
    }

    override fun getChatMessages(chatId: String): Flow<State<List<Message>>> {
        return messageRemoteDataSource.getChatMessages(chatId)
    }

    override fun checkIfNewChat(userEmail:String,receiverEmail: String): Flow<State<String>> {
        return messageRemoteDataSource.checkIfNewChat(userEmail,receiverEmail)
    }
}