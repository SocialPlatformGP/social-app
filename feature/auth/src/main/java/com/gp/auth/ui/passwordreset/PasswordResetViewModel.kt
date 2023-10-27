package com.gp.auth.ui.passwordreset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.auth.repo.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordResetViewModel @Inject constructor(private val repo: AuthenticationRepository) : ViewModel() {
    val uiState = MutableStateFlow(PasswordResetUiState())
    fun onSendResetEmail(){
        viewModelScope.launch {
            val state = repo.sendPasswordResetEmail(uiState.value.email)
            state.collect{
                uiState.value = uiState.value.copy(sentState = it)
            }
        }

    }
}