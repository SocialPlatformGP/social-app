package com.gp.chat.presentation.privateChat

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.Message
import com.gp.chat.model.NetworkMessage
import com.gp.chat.repository.MessageRepository
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
import com.gp.socialapp.utils.State
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val currentUser = Firebase.auth.currentUser
    private var CHAT_ID = MutableStateFlow("No Chat")
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages
    val currentMessage = MutableStateFlow(MessageState())
    var cleanedReceiverUserEmail = ""

    fun ReceiverEmail(email: String) {
        cleanedReceiverUserEmail = email
        if (cleanedReceiverUserEmail.contains("@")) {
            cleanedReceiverUserEmail = removeSpecialCharacters(email)
        }

        val cleanedCurrentUserEmail = removeSpecialCharacters(currentUser?.email!!)
        checkIfNewChat(cleanedCurrentUserEmail, cleanedReceiverUserEmail)
    }


    private fun checkIfNewChat(userEmail: String, receiverEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.checkIfNewChat(userEmail, receiverEmail).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        CHAT_ID.value = it.data
                        getMessages()
                    }

                    is State.Error -> {
                        Log.d("PrivateChatViewModel", "checkIfNewChat: ${it.message}")
                    }

                    is State.Loading -> {
                        //TODO handle loading
                    }

                    else -> {}
                }
            }
        }
    }

    fun getMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.getChatMessages(CHAT_ID.value).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        _messages.value = it.data
                    }

                    is State.Error -> {
                        //TODO handle error
                    }

                    is State.Loading -> {
                        //TODO handle loading
                    }

                    else -> {}
                }
            }
        }
    }


    fun sendMessage() {
        if (currentMessage.value.message.isEmpty()) {
            return
        } else if (CHAT_ID.value == "No Chat") {
            firstSendMessage()
        } else {
            send()
        }

    }

    fun send() {
        viewModelScope.launch(Dispatchers.IO) {
            CHAT_ID.value = messageRepository.sendMessage(
                CHAT_ID.value,
                NetworkMessage(
                    message = currentMessage.value.message,
                    messageDate = Date().toString(),
                    senderId = currentUser?.email ?: "",
                    senderName = currentUser?.displayName ?: "",
                    timestamp = Date().toString()
                )
            )
            updateRecent()
            getMessages()
        }
    }
    fun firstSendMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            CHAT_ID.value = messageRepository.sendMessage(
                CHAT_ID.value,
                NetworkMessage(
                    message = currentMessage.value.message,
                    messageDate = Date().toString(),
                    senderId = currentUser?.email ?: "",
                    senderName = currentUser?.displayName ?: "",
                    timestamp = Date().toString()
                )
            )

            createPrivateChat()
            updateRecent()
            getMessages()
        }
    }

    private fun createPrivateChat() {
        viewModelScope.launch { messageRepository.createNewChat(removeSpecialCharacters(currentUser?.email!!), cleanedReceiverUserEmail,CHAT_ID.value) }
    }

    fun updateRecent() {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.updateRecent(
                CHAT_ID.value,
                currentMessage.value.message,
                removeSpecialCharacters(currentUser?.email!!),
                removeSpecialCharacters(cleanedReceiverUserEmail)
            )
            currentMessage.value = currentMessage.value.copy(message = "")
        }
    }


}