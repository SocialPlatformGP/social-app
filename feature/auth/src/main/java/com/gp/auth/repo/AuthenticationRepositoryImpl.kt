package com.gp.auth.repo

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.gp.auth.network.AuthenticationRemoteDataSource
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val authenticationRemoteDataSource: AuthenticationRemoteDataSource,
): AuthenticationRepository {
    override fun signInUser(email: String, password: String): Flow<State<FirebaseUser>>
    = authenticationRemoteDataSource.signInUser(email, password)

    override fun signUpUser(email: String, password: String): Flow<State<FirebaseUser>>{
        return authenticationRemoteDataSource.signUpUser(email, password)
    }
}