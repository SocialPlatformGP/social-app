package com.gp.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.gp.auth.repo.AuthenticationRepository
import com.gp.auth.util.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepo: AuthenticationRepository) : ViewModel() {
    val loginStateFlow = MutableStateFlow<LoginUiState>(LoginUiState())
    fun onSignIn(){
        viewModelScope.launch {
            with(loginStateFlow.value){
                val state = authRepo.signInUser(email, password)
                state.collect{
                    loginStateFlow.value = loginStateFlow.value.copy(isSignedIn = it)
                }
            }
        }
    }
    fun authenticateWithGoogle(account: GoogleSignInAccount) = authRepo.authenticateWithGoogle(account)
}