package com.gp.chat.presentation.newchat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.ChatGroup
import com.gp.chat.repository.MessageRepository
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
import com.gp.socialapp.utils.State
import com.gp.users.model.User
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewChatViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {
    private val senderEmail = removeSpecialCharacters(Firebase.auth.currentUser?.email!!)
    val createNewChatState = MutableStateFlow<State<String>>(State.Idle)
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    init {
        getAllUsers()
    }

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = searchText.combine(_users) { text, users ->
        if (text.isBlank()) {
            users
        }
        users.filter { user ->
            "${user.firstName} ${user.lastName}".lowercase().contains(text.trim().lowercase())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _users.value
    )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    private fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.fetchUsers().collect {
                Log.d("TAG", "getAllUsers: $it")
                if (it is State.SuccessWithData) {
                    _users.value = it.data.filter { removeSpecialCharacters( it.email) == senderEmail }
                }
            }
        }
    }

    fun createChat(receiverEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.haveChatWithUser(senderEmail, removeSpecialCharacters(receiverEmail))
                .collect {
                    createNewChatState.value = it
                    when (it) {
                        is State.SuccessWithData -> {
                            Log.d("TAG", "createChat: ${it.data}")
                            if (it.data == "-1") {
                                insertNewChat(receiverEmail)


                            } else {
                                //todo complete

                            }
                        }

                        is State.Error -> {
                            Log.d("TAG", "createChat: ${it.message}")
                        }

                        else -> {}
                    }
                }
        }
    }

    fun insertNewChat(receiverEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.insertChat(
                ChatGroup(
                    name = "Private Chat",
                    members = mapOf(
                        senderEmail to true,
                        removeSpecialCharacters(receiverEmail) to true
                    )
                )
            ).collect {
                createNewChatState.value = it
                if (it is State.SuccessWithData) {
                    insertUserToChat(senderEmail, it.data, removeSpecialCharacters(receiverEmail))
                    insertPrivateChat(receiverEmail, it.data)
                }

            }
        }
    }

    fun insertUserToChat(senderEmail: String, chatId: String, receiverEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.insertChatToUser(
                chatId,
                removeSpecialCharacters(senderEmail),
                receiverEmail
            ).collect {
                createNewChatState.value = it
            }
        }
    }

    fun insertPrivateChat(receiverEmail: String, chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.insertPrivateChat(
                senderEmail,
                removeSpecialCharacters(receiverEmail),
                chatId
            ).collect {
                createNewChatState.value = it

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        createNewChatState.value = State.Idle
        _users.value = emptyList()
    }


}

data class NewChatSearchState(
    val isSearching: Boolean = false,
    val searchText: String = "",
    val matchingMembers: List<User> = emptyList()
)