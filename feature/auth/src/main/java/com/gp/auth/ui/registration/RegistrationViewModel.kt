package com.gp.auth.ui.registration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.auth.repo.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class RegistrationViewModel @Inject constructor(private val authRepo: AuthenticationRepository) : ViewModel() {
    val registrationUiState = MutableStateFlow(RegistrationUiState())
    fun onSignUp(){
        viewModelScope.launch {
            with(registrationUiState.value){
                val state = authRepo.signUpUser(email, password)
                state.collect{
                    registrationUiState.value = registrationUiState.value.copy(isSignedUp = it)
                }
            }
        }
    }
}