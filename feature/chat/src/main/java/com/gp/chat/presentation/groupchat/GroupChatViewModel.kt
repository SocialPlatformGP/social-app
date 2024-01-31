package com.gp.chat.presentation.groupchat

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.Message
import com.gp.chat.model.RecentChat
import com.gp.chat.presentation.privateChat.MessageState
import com.gp.chat.repository.MessageRepository
import com.gp.socialapp.utils.State
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    private val messageRepo: MessageRepository,
    private val userRepo: UserRepository
) : ViewModel() {
    private val _messages = MutableStateFlow(emptyList<Message>())
    val messages = _messages.asStateFlow()
    private val _currentMessageState = MutableStateFlow(MessageState())
    val currentMessageState = _currentMessageState.asStateFlow()
    val currentUser = Firebase.auth.currentUser!!
    private lateinit var chatId: String
    private lateinit var title: String
    private lateinit var photoUrl: String

    fun setData(chatId: String, title: String, photoUrl: String) {
        this.chatId = chatId
        this.title = title
        this.photoUrl = photoUrl
        Log.d("zarea1", "setData: getting chat id $chatId")
        fetchGroupChatMessages()
    }

    fun fetchGroupChatMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("zarea1", "fetchGroupChatMessages: getting chat id $chatId")

            messageRepo.fetchGroupChatMessages(chatId).collect {
                _messages.value = it
                Log.d("zarea1", "incoming messages: $it")
            }
        }
    }
    fun updateCurrentMessage(message: String){
        viewModelScope.launch (Dispatchers.IO){
            _currentMessageState.value = _currentMessageState.value.copy(message = message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSendMessage() {
        Log.d("zarea2", "start of message send  $chatId")
        if (currentMessageState.value.message.isEmpty() && currentMessageState.value.fileType == "") {
            return
        } else {
            val currentTime: ZonedDateTime =now()
            val  formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH)
            val formatted = currentTime.format(formatter)

            viewModelScope.launch(Dispatchers.IO) {
                val message = Message(
                    id = "",
                    groupId = chatId,
                    message = currentMessageState.value.message,
                    messageDate = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
                        .format(currentTime),
                    senderId = currentUser.email!!,
                    senderName = currentUser.displayName!!,
                    senderPfpURL = currentUser.photoUrl.toString(),
                    timestamp = formatted,
                    fileURI = currentMessageState.value.fileUri ?: "".toUri(),
                    fileType = currentMessageState.value.fileType ?: "",
                    fileNames = currentMessageState.value.fileName ?: ""
                )
                val recentChat = RecentChat(
                    lastMessage = if (currentMessageState.value.fileType == "") {
                        "${currentUser.displayName}: ${currentMessageState.value.message}"
                    } else {
                        "${currentUser.displayName}: ${currentMessageState.value.fileName}"
                    },
                    timestamp = message.timestamp
                )
                messageRepo.sendGroupMessage(message, recentChat).collect {
                    when (it) {
                        is State.Success -> {
                            Log.d("zarea2", "in success   $chatId")
                            _currentMessageState.value = MessageState()
                            Log.d("seerde", "Success")
                        }


                        is State.Error -> {
                            Log.d("seerde", "Error: ${it.message}")
                        }


                        else -> {
                            Log.d("seerde", "Loading")
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)

    private fun updateRecent() {
        val currentTime: ZonedDateTime = now()

        viewModelScope.launch(Dispatchers.IO) {
            val recentChat = RecentChat(
                lastMessage =
                if (currentMessageState.value.fileType == "text") {
                    currentMessageState.value.message
                } else {
                    currentMessageState.value.fileName
                },
                timestamp = currentTime.toString(),

                )
            Log.d("zarea2", "calling update recent in vm  $chatId")
            messageRepo.updateRecentChat(recentChat, chatId).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        Log.d("zarea2", "successful with data ${it.data}")
                        _currentMessageState.value =
                            _currentMessageState.value.copy(error = "recent updated")
                        Log.d("zarea2", "data in state before ${currentMessageState.value}")
                        _currentMessageState.value = MessageState()
                        Log.d("zarea2", "data in state after ${currentMessageState.value}")
                    }

                    is State.Error -> {
                        _currentMessageState.value =
                            _currentMessageState.value.copy(error = it.message)
                    }

                    is State.Loading -> {
                        _currentMessageState.value =
                            _currentMessageState.value.copy(error = "recent updating")
                    }

                    else -> {}
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendFile(uri: Uri, type: String, fileName: String) {
        _currentMessageState.value = _currentMessageState.value.copy(
            fileName = fileName,
            fileUri = uri,
            fileType = type
        )
        onSendMessage()
    }

    fun deleteMessage(messageId: String, chatId: String) {
        messageRepo.deleteMessage(messageId, chatId)
    }

    fun updateMessage(messageId: String, chatId: String, updatedText: String) {
        messageRepo.updateMessage(messageId, chatId, updatedText)
    }
}