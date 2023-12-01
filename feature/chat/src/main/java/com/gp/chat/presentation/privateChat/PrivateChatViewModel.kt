package com.gp.chat.presentation.privateChat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.Message
import com.gp.chat.model.RecentChat
import com.gp.chat.repository.MessageRepository
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
import com.gp.chat.util.RemoveSpecialChar.restoreOriginalEmail
import com.gp.socialapp.utils.State
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class PrivateChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private var currentEmail = removeSpecialCharacters(Firebase.auth.currentUser?.email!!)
    private var ChatId = "-1"
    private var receiverEmail = ""
    private var senderEmail = ""
    val currentMessage = MutableStateFlow(MessageState())
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()


    fun setChatId(chatId: String) {
        ChatId = chatId
        getMessages()
    }

    fun setReceiverEmail(email: String) {
        receiverEmail = email
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
                    senderId = currentEmail,
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
    private fun updateRecent(){
        viewModelScope.launch (Dispatchers.IO){
            val recentChat = RecentChat(
                lastMessage = currentMessage.value.message,
                timestamp = Date().toString(),
                title = "private chat",
                privateChat = true,
                receiverName = receiverEmail,
                senderName = senderEmail,


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



fun deleteMessage(messageId: String, chatId: String){
    messageRepository.deleteMessage(messageId, chatId)
}
    fun updateMessage(messageId: String, chatId: String,updatedText:String){
        messageRepository.updateMessage(messageId, chatId,updatedText)
    }

}