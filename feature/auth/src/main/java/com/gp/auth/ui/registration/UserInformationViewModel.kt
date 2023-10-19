package com.gp.auth.ui.registration

import androidx.lifecycle.ViewModel
import com.example.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class UserInformationViewModel @Inject constructor(private val userRepo: UserRepository) : ViewModel() {
    val uiState = MutableStateFlow(UserInformationUIState())
}