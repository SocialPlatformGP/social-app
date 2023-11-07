package com.gp.chat.presentation.privateChat

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.ChatGroup
import com.gp.chat.model.ChatUser
import com.gp.chat.model.Message
import com.gp.chat.model.NetworkMessage
import com.gp.chat.model.PrivateChats
import com.gp.chat.model.PrivateChatsNetwork
import com.gp.chat.model.RecentChat
import com.gp.chat.repository.MessageRepository
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
import com.gp.socialapp.utils.State
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.Async
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class PrivateChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {
    private val senderEmail = removeSpecialCharacters(Firebase.auth.currentUser?.email!!)
    private var ChatId = "-1"
    private var receiverEmail = ""
    val currentMessage = MutableStateFlow(MessageState())
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()


    fun setChatId(chatId: String) {
        ChatId = chatId
        getMessages()
    }

    fun setReceiverEmail(email: String) {
        receiverEmail = removeSpecialCharacters(email)
    }

    fun getMessages() {
            viewModelScope.launch {
                messageRepository.getMessages(ChatId).collect {
                    when (it) {
                        is State.SuccessWithData -> {
                            _messages.value = it.data
                            currentMessage.value = currentMessage.value.copy(error = "data loaded")
                        }
                        is State.Error -> { currentMessage.value = currentMessage.value.copy(error = it.message) }
                        is State.Loading -> { currentMessage.value = currentMessage.value.copy(error = " getting messages") }
                        else -> {}
                    }
                }
            }
    }
    fun sendMessage() {
        Log.d("testo vm", "sendMessage start: ${currentMessage.value.message}")
        if (currentMessage.value.message.isEmpty()) {
            currentMessage.value = currentMessage.value.copy(error = "message is empty")
            return
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val message = Message(
                    senderId = senderEmail,
                    groupId = ChatId,
                    message = currentMessage.value.message,
                    timestamp = Date().toString(),
                )
                messageRepository.sendMessage(message).collect{
                    when (it) {
                        is State.SuccessWithData -> {
                            currentMessage.value = currentMessage.value.copy(error = "message sent")
                            updateRecent()
                        }
                        is State.Error -> { currentMessage.value = currentMessage.value.copy(error = it.message) }
                        is State.Loading -> { currentMessage.value = currentMessage.value.copy(error = "sending message") }
                        else -> {}
                    }
                }
            }
        }
    }
    fun updateRecent(){
        viewModelScope.launch (Dispatchers.IO){
            val recentChat = RecentChat(
                lastMessage = currentMessage.value.message,
                timestamp = Date().toString(),
                title = "private chat",
                isPrivateChat = true,

            )
            messageRepository.updateRecentChat(recentChat,ChatId).collect{
                when (it) {
                    is State.SuccessWithData -> {
                        currentMessage.value = currentMessage.value.copy(error = "recent updated")
                        currentMessage.value = currentMessage.value.copy(error = "recent updated",message = "")

                    }
                    is State.Error -> { currentMessage.value = currentMessage.value.copy(error = it.message) }
                    is State.Loading -> { currentMessage.value = currentMessage.value.copy(error = "recent updated") }
                    else -> {}
                }
            }
        }
    }


}