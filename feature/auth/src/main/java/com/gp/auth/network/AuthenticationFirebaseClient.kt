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
import javax.inject.Inject

class AuthenticationFirebaseClient @Inject constructor(private val auth: FirebaseAuth): AuthenticationRemoteDataSource {
    override fun signInUser(email: String, password: String): Flow<State<FirebaseUser>> = flow{
        emit(State.Idle)
        try {
            val task = auth.signInWithEmailAndPassword(email, password)
            emit(State.SuccessWithData(task.result.user!!))
        } catch(exception: Exception){
            emit(State.Error("Login Failed: ${exception.message}"))
        }
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithEmail:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext,
//                        "Authentication failed.",
//                        Toast.LENGTH_SHORT,
//                    ).show()
//                    updateUI(null)
//                }
//            }
    }
}