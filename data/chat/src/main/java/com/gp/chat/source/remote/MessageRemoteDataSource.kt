package com.gp.chat.source.remote

import com.gp.chat.model.GroupMessage
import com.gp.chat.model.Message
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow

interface MessageRemoteDataSource {
    suspend fun sendMessage(chatId: String, message: Message)
    fun getChatMessages(chatId: String): Flow<List<Message>>
    fun fetchGroupChatMessages(groupId: String): Flow<List<GroupMessage>>
    fun sendGroupMessage(groupId: String, message: GroupMessage): Flow<State<Nothing>>
}