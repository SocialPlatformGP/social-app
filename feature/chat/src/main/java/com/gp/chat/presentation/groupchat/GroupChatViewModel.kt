package com.gp.chat.presentation.groupchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.chat.model.GroupMessage
import com.gp.chat.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor (private val messageRepo: MessageRepository) : ViewModel() {
    private val _chatMessagesFlow = MutableStateFlow(emptyList<GroupMessage>())
    val chatMessagesFlow = _chatMessagesFlow.asStateFlow()
    fun fetchGroupChatMessages(groupId: String){
        viewModelScope.launch(Dispatchers.IO) {
            messageRepo.fetchGroupChatMessages(groupId).collectLatest{
                _chatMessagesFlow.value = it
            }
        }
    }
}