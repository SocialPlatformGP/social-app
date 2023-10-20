package com.gp.auth.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.users.model.NetworkUser
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class UserInformationViewModel @Inject constructor(private val userRepo: UserRepository) : ViewModel() {
    val uiState = MutableStateFlow(UserInformationUIState())
    private val pfpLink = "https://clipart-library.com/data_images/6103.png"
    fun onCompleteAccount(email: String, password: String){
        viewModelScope.launch {
            with(uiState.value) {
                val networkFlow =
                    userRepo.createNetworkUser(NetworkUser(firstName, lastName, password, pfpLink, email,
                        phoneNumber, birthDate, bio, Date()))
                networkFlow.collect{state ->
                    uiState.value = uiState.value.copy(createdState = state)
                }
            }
        }
    }
}