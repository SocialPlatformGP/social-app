package com.gp.chat.presentation.privateChat

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.Message
import com.gp.chat.model.RecentChat
import com.gp.chat.repository.MessageRepository
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
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
    private var currentUser = Firebase.auth.currentUser
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
        if (currentMessage.value.message.isEmpty() && currentMessage.value.fileTypes=="text") {
            currentMessage.value = currentMessage.value.copy(error = "message is empty")
            return
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                if(currentMessage.value.fileTypes!="text"){
                    currentMessage.value = currentMessage.value.copy(message = currentMessage.value.fileTypes)
                }
                val message = Message(
                    senderId = currentEmail,
                    senderName = currentUser?.displayName?:"",
                    senderPfpURL = currentUser?.photoUrl.toString(),
                    groupId = ChatId,
                    message = currentMessage.value.message,
                    timestamp = Date().toString(),
                    fileURI = currentMessage.value.fileUri?:"".toUri(),
                    fileType = currentMessage.value.fileTypes?:"", //TODO: change to file type
                    fileNames = currentMessage.value.fileName?:""
                )

                    messageRepository.sendMessage(message,currentUser).collect {
                        when (it) {
                            is State.SuccessWithData -> {
                                updateRecent()
                            }

                            is State.Error -> {
                                currentMessage.value = currentMessage.value.copy(error = it.message)
                            }

                            is State.Loading -> {
                                currentMessage.value =
                                    currentMessage.value.copy(error = "sending message")
                            }

                            else -> {}
                        }
                    }

            }
        }
    }
    fun sendImage(uri: Uri,type:String,fileName:String){
        currentMessage.value = currentMessage.value.copy(fileName = fileName)
        currentMessage.value = currentMessage.value.copy(fileUri = uri)
        currentMessage.value = currentMessage.value.copy(fileTypes = type)
        sendMessage()
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