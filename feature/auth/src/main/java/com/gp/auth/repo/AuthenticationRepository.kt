package com.gp.auth.repo

import com.google.firebase.auth.FirebaseUser
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    fun signInUser(email: String, password: String): Flow<State<FirebaseUser>>
    fun signUpUser(email: String, password: String): Flow<State<FirebaseUser>>
}