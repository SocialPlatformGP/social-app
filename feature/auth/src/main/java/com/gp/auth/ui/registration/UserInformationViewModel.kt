package com.gp.auth.ui.registration

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.users.model.NetworkUser
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class UserInformationViewModel @Inject constructor(private val userRepo: UserRepository) :
    ViewModel() {
    val uiState = MutableStateFlow(UserInformationUIState())
    fun onCompleteAccount(email: String, password: String) {
        viewModelScope.launch {
            with(uiState.value) {
                val networkFlow =
                    userRepo.createNetworkUser(
                        NetworkUser(
                            firstName, lastName, password, "", email,
                            phoneNumber, birthDate, bio, Date()
                        ), pfpLocalURI)
                networkFlow.collect { state ->
                    uiState.value = uiState.value.copy(createdState = state)
                }
            }
        }
    }
    fun onFirstNameChange(firstName: String) {
        uiState.update { it.copy(firstName = firstName) }
    }
    fun onLastNameChange(lastName: String) {
        uiState.update { it.copy(lastName = lastName) }
    }
    fun onPhoneNumberChange(phoneNumber: String) {
        uiState.update { it.copy(phoneNumber = phoneNumber) }
    }
    fun onBirthDateChange(birthDate: Date) {
        uiState.update { it.copy(birthDate = birthDate) }
    }
    fun onBioChange(bio: String) {
        uiState.update { it.copy(bio = bio) }
    }
}