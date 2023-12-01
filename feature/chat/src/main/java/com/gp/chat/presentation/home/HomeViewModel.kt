package com.gp.chat.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.RecentChat
import com.gp.chat.repository.MessageRepository
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
import com.gp.socialapp.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val messageRepository: MessageRepository
):ViewModel(){

    private val userEmail =Firebase.auth.currentUser?.email!!

    private val _recentChats = MutableStateFlow<List<RecentChat>>(emptyList())
    val recentChats = _recentChats.asStateFlow()
    private val chats = mutableListOf("-1")
    init{
        getChatsForUser()
    }

    private fun getChatsForUser() {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.getUserChats(
                removeSpecialCharacters(userEmail)
            ).collect {
                when(it){
                    is State.SuccessWithData->{
                        getRecentChats(it.data.groups.keys.toList())

                    }
                    is State.Error->{

                    }
                    is State.Loading->{

                    }
                    else-> {

                    }
                }
            }

        }
    }

    fun getRecentChats(chatId:List<String>){
        Log.d("testoVmHome", "getRecentChats: $chatId")
        viewModelScope.launch {
            messageRepository.getRecentChats(chatId).collect{
                when(it){
                    is State.SuccessWithData->{
                        Log.d("testoVmHome", "getRecentChats: ${it.data}")
                        _recentChats.value=it.data
                    }
                    is State.Error->{

                    }
                    is State.Loading->{

                    }
                    else-> {

                    }
                }
            }
        }

    }
    fun leaveGroup(chatId: String){
        messageRepository.leaveGroup(chatId)
    }
}