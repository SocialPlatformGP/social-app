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
import com.gp.socialapp.utils.State
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class PrivateChatViewModel @Inject constructor (
    private val messageRepository: MessageRepository
): ViewModel() {
    private val currentUser =Firebase.auth.currentUser
    private var TAG = "123456"
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages=_messages
    val currentMessage=MutableStateFlow(MessageState())




    fun getMessages(){
        if(TAG.isEmpty()){
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.getChatMessages(TAG).collect{
                when(it){
                    is State.SuccessWithData -> {
                        _messages.value = it.data
                    }
                    is State.Error -> {
                        //TODO handle error
                    }
                    is State.Loading -> {
                        //TODO handle loading
                    }
                    else->{}
                }
            }
        }
    }
    fun sendMessage(){
        if(currentMessage.value.message.isEmpty()){
            return
        }
        else {
            viewModelScope.launch(Dispatchers.IO) {
                TAG=messageRepository.sendMessage(
                    TAG,
                    NetworkMessage(
                        message = currentMessage.value.message,
                        messageDate = Date().toString(),
                        senderId = currentUser?.email ?: "",
                        senderName = currentUser?.displayName ?: "",
                        timestamp = Date().toString()
                    )
                )
                currentMessage.value=currentMessage.value.copy(message = "")
            }

        }
    }


}