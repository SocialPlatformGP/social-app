package com.gp.auth.ui

import androidx.lifecycle.ViewModel
import com.gp.auth.repo.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel@Inject constructor(private val repo: AuthenticationRepository) : ViewModel(){
    fun getSignedInUser() = repo.getSignedInUser()
}