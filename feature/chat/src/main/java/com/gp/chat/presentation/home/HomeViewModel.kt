package com.gp.chat.presentation.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
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
    private val _recentChats = MutableStateFlow<List<RecentChat>>(emptyList())
    val recentChats = _recentChats.asStateFlow()
    private val _chatHomeState = MutableStateFlow(
        ChatHomeState(
            Firebase.auth.currentUser!!
        )
    )
    val chatHomeState = _chatHomeState.asStateFlow()

    init {
        getChatsForUser()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getChatsForUser() {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.getUserChats(
                removeSpecialCharacters(chatHomeState.value.currentUser.email!!)
            ).collect { result ->
                when (result) {
                    is State.SuccessWithData -> {
                        getRecentChats(result.data.groups.keys.toList())
                    }

                    is State.Error -> {
                        _chatHomeState.value = _chatHomeState.value.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = result.message
                        )
                    }

                    is State.Loading -> {
                        _chatHomeState.value = _chatHomeState.value.copy(
                            isLoading = true
                        )
                    }

                    else -> {}
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getRecentChats(chatId: List<String>) {
        Log.d("testoVmHome", "getRecentChats: $chatId")
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH)

            messageRepository.getRecentChats(chatId).collect { result ->
                when (result) {
                    is State.SuccessWithData -> {
                        Log.d("testoVmHome", "getRecentChats: ${result.data}")
                        if (result.data.isNotEmpty()) {
                            _recentChats.value = result.data.sortedByDescending {
                                ZonedDateTime.parse(
                                    it.timestamp,
                                    formatter
                                )
                            }
                            _chatHomeState.value = _chatHomeState.value.copy(
                                isLoading = false,
                                isError = false
                            )
                        }
                    }

                    is State.Error -> {
                        _chatHomeState.value = _chatHomeState.value.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = result.message
                        )
                    }

                    is State.Loading -> {
                        _chatHomeState.value = _chatHomeState.value.copy(
                            isLoading = true
                        )
                    }

                    else -> {}
                }
            }
        }
    }

    fun leaveGroup(chatId: String) {
        messageRepository.leaveGroup(chatId)
    }
}

data class ChatHomeState(
    var currentUser: FirebaseUser,
    var isLoading: Boolean = false,
    var isError: Boolean = false,
    val errorMessage: String = ""
)