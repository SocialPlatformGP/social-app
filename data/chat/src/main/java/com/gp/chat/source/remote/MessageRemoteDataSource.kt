package com.gp.chat.source.remote

import com.gp.chat.model.Message
import com.gp.chat.model.NetworkMessage
import kotlinx.coroutines.flow.Flow

interface MessageRemoteDataSource {
    suspend fun sendMessage(chatId: String, message: NetworkMessage): String
    fun getChatMessages(chatId: String): Flow<List<Message>>
}