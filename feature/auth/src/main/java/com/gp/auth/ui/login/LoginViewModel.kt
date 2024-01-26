package com.gp.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.gp.auth.repo.AuthenticationRepository
import com.gp.auth.util.Validator
import com.gp.socialapp.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepo: AuthenticationRepository) : ViewModel() {
    val loginStateFlow = MutableStateFlow(LoginUiState())
    fun onSignIn(){
        with(loginStateFlow.value){
            if(email.length< 6 || !Validator.EmailValidator.validateAll(email)){
                loginStateFlow.value = loginStateFlow.value.copy(emailError = "Invalid Email")
                return
            }
            else{
                loginStateFlow.value = loginStateFlow.value.copy(emailError = "")
            }
            if(password.length < 6 || !Validator.PasswordValidator.validateAll(password)){
                loginStateFlow.value = loginStateFlow.value.copy(passwordError = "Invalid Password")
                return
            }
            else{
                loginStateFlow.value = loginStateFlow.value.copy(passwordError = "")
            }
        }
        viewModelScope.launch {
            with(loginStateFlow.value){
                val state = authRepo.signInUser(email, password)
                state.collect{
                    loginStateFlow.value = loginStateFlow.value.copy(isSignedIn = it, passwordError = "", emailError = "")
                    if(it is State.Error){
                        loginStateFlow.value = loginStateFlow.value.copy(passwordError = it.message)
                    }
                }
            }
        }
    }
    fun authenticateWithGoogle(account: GoogleSignInAccount) = authRepo.authenticateWithGoogle(account)

    fun updateEmail(email: String){
        loginStateFlow.update { it.copy(email = email) }
    }
    fun updatePassword(password: String){
        loginStateFlow.update { it.copy(password = password) }
    }
}