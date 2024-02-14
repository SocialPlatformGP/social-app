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
import com.gp.chat.model.ChatGroup
import com.gp.chat.model.Message
import com.gp.chat.model.RecentChat
import com.gp.chat.presentation.privateChat.MessageState
import com.gp.chat.repository.MessageRepository
import com.gp.chat.util.RemoveSpecialChar
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
) : ViewModel() {
    private val _messages = MutableStateFlow(emptyList<Message>())
    private val _groupDetails = MutableStateFlow(ChatGroup())
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
        fetchGroupDetails()
    }
    private fun fetchGroupDetails(){
        viewModelScope.launch (Dispatchers.IO) {
            messageRepo.getGroupDetails(chatId).collect{
                when(it){
                    is State.SuccessWithData -> {
                        _groupDetails.value = it.data
                    }
                    is State.Error -> {
                        Log.d("SEERDE", "fetchGroupDetails: ${it.message}")
                    }
                    else -> {}
                }
            }
        }
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
    fun checkIfAdmin() : Boolean {
        val map = _groupDetails.value.members
        val key = RemoveSpecialChar.removeSpecialCharacters(currentUser.email?:"")
        return map.containsKey(key) && map.getValue(key)
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
                    groupId = chatId,
                    message = currentMessageState.value.message,
                    messageDate = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
                        .format(currentTime),
                    senderId = currentUser.email!!,
                    senderName = currentUser.displayName!!,
                    senderPfpURL = currentUser.photoUrl.toString(),
                    timestamp = formatted,
                    fileURI = currentMessageState.value.fileUri,
                    fileType = currentMessageState.value.fileType,
                    fileNames = currentMessageState.value.fileName
                )
                messageRepo.sendMessage(message).collect {
                    when (it) {
                        is State.SuccessWithData -> {
                            Log.d("zarea2", "in success   $chatId")
                            updateRecent(message.timestamp)
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

    private fun updateRecent(timestamp: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val recentChat = RecentChat(
                lastMessage =
                if (currentMessageState.value.fileType == "") {
                    currentMessageState.value.message
                } else {
                    currentMessageState.value.fileName
                },
                timestamp = timestamp,
                )
            Log.d("zarea2", "calling update recent in vm  $chatId")
            messageRepo.updateRecentChat(recentChat, chatId).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        Log.d("zarea2", "data in state before ${currentMessageState.value}")
                        _currentMessageState.value = MessageState()
                        Log.d("zarea2", "data in state after ${currentMessageState.value}")
                    }
                    is State.Error -> {
                        _currentMessageState.value =
                            _currentMessageState.value.copy(error = it.message)
                    }
                    is State.Loading -> {}
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

    fun deleteMessage(messageId: String,) {
        messageRepo.deleteMessage(messageId, chatId)
    }

    fun updateMessage(messageId: String, updatedText: String) {
        //TODO(UPDATE RECENT CHAT)
        messageRepo.updateMessage(messageId, chatId, updatedText)
    }
}