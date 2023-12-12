package com.gp.chat.presentation.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val userEmail = Firebase.auth.currentUser?.email!!

    private val _recentChats = MutableStateFlow<List<RecentChat>>(emptyList())
    val recentChats = _recentChats.asStateFlow()

    init {
        getChatsForUser()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getChatsForUser() {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.getUserChats(
                removeSpecialCharacters(userEmail)
            ).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        getRecentChats(it.data.groups.keys.toList())

                    }

                    is State.Error -> {

                    }

                    is State.Loading -> {

                    }

                    else -> {

                    }
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getRecentChats(chatId: List<String>) {
        Log.d("testoVmHome", "getRecentChats: $chatId")
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH)

            messageRepository.getRecentChats(chatId).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        Log.d("testoVmHome", "getRecentChats: ${it.data}")
                        if (it.data.isNotEmpty()) {
                            _recentChats.value =

                                it.data.sortedByDescending { ZonedDateTime.parse(it.timestamp, formatter) }
                        }
                    }

                    is State.Error -> {

                    }

                    is State.Loading -> {

                    }

                    else -> {

                    }
                }
            }
        }

    }

    fun leaveGroup(chatId: String) {
        messageRepository.leaveGroup(chatId)
    }
}