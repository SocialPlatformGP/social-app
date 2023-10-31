package com.gp.chat.source.remote

import com.gp.chat.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRemoteDataSource {
    suspend fun sendMessage(chatId: String, message: Message)
    fun getChatMessages(chatId: String): Flow<List<Message>>
}