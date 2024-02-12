package com.gp.chat.presentation.privateChat

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.Message
import com.gp.chat.model.RecentChat
import com.gp.chat.repository.MessageRepository
import com.gp.socialapp.utils.State
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
class PrivateChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
) : ViewModel() {
    private lateinit var chatId: String
    private lateinit var senderName: String
    val currentUser = Firebase.auth.currentUser!!
    private lateinit var receiverName: String
    private lateinit var senderPic: String
    private lateinit var receiverPic: String
    val currentMessage = MutableStateFlow(MessageState())
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun setData(
        chatId: String,
        senderName: String,
        receiverName: String,
        senderPic: String,
        receiverPic: String
    ) {
        this.chatId = chatId
        this.senderName = senderName
        this.receiverName = receiverName
        this.senderPic = senderPic
        this.receiverPic = receiverPic
        getMessages()

    }
    fun setCurrentMessage(message: String){
        viewModelScope.launch {
            currentMessage.value = currentMessage.value.copy(message = message)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMessages() {
        Log.d("SEERDE", "getMessages: called $chatId")
        viewModelScope.launch(Dispatchers.IO) {
                        val currentTime: ZonedDateTime =now()
            val  formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH)
            val formatted = currentTime.format(formatter)
            messageRepository.getMessages(chatId).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        Log.d("SEERDE", "getMessages: ${it.data}")
                        _messages.value = it.data
                    }

                    is State.Error -> {
                        Log.d("SEERDE", "getMessages: ${it.message}")
                        if (it.message == "No messages found") {
                            messageRepository.insertRecentChat(
                                RecentChat(
                                    id = "",
                                    senderName = senderName,
                                    senderPicUrl = senderPic,
                                    receiverName = receiverName,
                                    receiverPicUrl = receiverPic,
                                    lastMessage = "No messages yet",
                                    timestamp = formatted,
                                    title = "private chat",
                                    privateChat = true
                                ), chatId
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage() {
        if (currentMessage.value.message.isEmpty() && currentMessage.value.fileType == "") {
            return
        } else {
            val currentTime: ZonedDateTime =now()
            val  formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH)
            val formatted = currentTime.format(formatter)
            viewModelScope.launch(Dispatchers.IO) {
                val message = Message(
                    senderId = currentUser.email!!,
                    senderName = currentUser.displayName!!,
                    senderPfpURL = currentUser.photoUrl.toString(),
                    groupId = chatId,
                    messageDate = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH).format(currentTime),
                    message = currentMessage.value.message,
                    timestamp = formatted,
                    fileURI = currentMessage.value.fileUri ,
                    fileType = currentMessage.value.fileType ,
                    fileNames = currentMessage.value.fileName
                )

                messageRepository.sendMessage(message).collect {
                    if (it is State.SuccessWithData) {
                        updateRecent(message.timestamp)
                    }

                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendFile(uri: Uri, type: String, fileName: String) {
        currentMessage.value = currentMessage.value.copy(
            fileName = fileName,
            fileUri = uri,
            fileType = type
        )
        sendMessage()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateRecent(timestamp: String) {

        viewModelScope.launch (Dispatchers.IO){
            val recentChat = RecentChat(
                lastMessage =
                if (currentMessage.value.fileType == "") {
                    currentMessage.value.message
                } else {
                    currentMessage.value.fileName
                },
                timestamp = timestamp,
                title = "private chat",
                privateChat = true,
                receiverName = receiverName,
                senderName = senderName,
                receiverPicUrl = receiverPic,
                senderPicUrl = senderPic,
                )
            messageRepository.updateRecentChat(recentChat, chatId).collect {
                if (it is State.SuccessWithData) {
                    currentMessage.value = MessageState()
                }

            }
        }
    }


    fun deleteMessage(messageId: String,) {
        messageRepository.deleteMessage(messageId, chatId)
    }

    fun updateMessage(messageId: String, updatedText: String) {
        messageRepository.updateMessage(messageId, chatId, updatedText)
    }

}