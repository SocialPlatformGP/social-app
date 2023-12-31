package com.gp.auth.repo

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    fun signInUser(email: String, password: String): Flow<State<FirebaseUser>>
    fun signUpUser(email: String, password: String): Flow<State<FirebaseUser>>
    fun getSignedInUser(): FirebaseUser?
    fun authenticateWithGoogle(account: GoogleSignInAccount): Flow<State<FirebaseUser>>
    fun sendPasswordResetEmail(email: String): Flow<State<Nothing>>
}