package com.gp.chat.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.RecentChat
import com.gp.chat.repository.MessageRepository
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
import com.gp.socialapp.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
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
    init{
        getRecentChats()
    }
    fun getRecentChats(){
        viewModelScope.launch {
            messageRepository.getRecentChats(removeSpecialCharacters(userEmail)).collect{
                when(it){
                    is State.SuccessWithData->{
                        _recentChats.value=it.data
                    }
                    is State.Error->{
                        //TODO
                    }
                    is State.Loading->{
                        //TODO
                    }
                    else-> {

                    }
                }
            }
        }

    }
}