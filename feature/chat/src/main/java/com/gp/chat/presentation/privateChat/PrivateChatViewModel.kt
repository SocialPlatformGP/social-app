package com.gp.chat.presentation.privateChat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.Message
import com.gp.chat.repository.MessageRepository
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
    private var TAG = "ChatID1"
    private val _messages = MutableStateFlow(listOf<Message>())
    val messages=_messages

    val currentMessage=MutableStateFlow(MessageState())



    fun getMessages(){
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.getChatMessages(TAG).collect{
                _messages.value = it
            }
        }
    }
    fun setTag(tag:String){
        TAG=tag
    }
    fun sendMessage(){
        if(currentMessage.value.message.isEmpty()){
            return
        }
        else {
            viewModelScope.launch(Dispatchers.IO) {
                messageRepository.sendMessage(
                    TAG,
                    Message(
                        "",
                        currentMessage.value.message,
                        currentUser?.email ?: "",
                        "2",
                        Date().toString()
                    )
                )
                currentMessage.value=currentMessage.value.copy(message = "")
            }

        }
    }


}