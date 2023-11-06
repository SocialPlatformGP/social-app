package com.gp.chat.presentation.createGroupChat

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class CreateGroupChatViewModel @Inject constructor(): ViewModel() {
    val uiState = MutableStateFlow(CreateGroupChatUiState())
}