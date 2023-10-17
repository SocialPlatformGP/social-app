package com.gp.auth.network

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationFirebaseClient @Inject constructor(private val auth: FirebaseAuth): AuthenticationRemoteDataSource {
    override fun signInUser(email: String, password: String): Flow<State<FirebaseUser>> = flow{
        emit(State.Loading)
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            emit(State.SuccessWithData(result.user!!))
        } catch(exception: Exception){
            emit(State.Error(exception.message!!))
        }
    }

    override fun signUpUser(email: String, password: String) = flow{
        emit(State.Loading)
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            emit(State.SuccessWithData(result.user!!))
        } catch(exception: Exception){
            emit(State.Error(exception.message!!))
        }
    }
}