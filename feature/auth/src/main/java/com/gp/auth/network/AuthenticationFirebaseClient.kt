package com.gp.auth.network

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.socialapp.utils.State
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthenticationFirebaseClient @Inject constructor(private val auth: FirebaseAuth): AuthenticationRemoteDataSource {
    override fun signInUser(email: String, password: String): Flow<State<FirebaseUser>> =
        callbackFlow {
            trySend(State.Loading)
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    trySend(State.SuccessWithData(it.user!!))
                }.addOnFailureListener {
                    Log.d(
                        "Edrees", "Cause: ${(it as FirebaseException).cause}, " +
                                "LocalizedMessage: ${it.localizedMessage}, " +
                                "Stacktrace: ${it.stackTrace}, " +
                                "Message: ${it.message}"
                    )
                    trySend(State.Error(it.message!!))
                }
            awaitClose()
        }

    override fun signUpUser(email: String, password: String): Flow<State<FirebaseUser>> =
        callbackFlow {
            trySend(State.Loading)
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    trySend(State.SuccessWithData(it.user!!))
                }.addOnFailureListener {
                    trySend(State.Error((it.message!!)))
                }
            awaitClose()
        }
    override fun sendPasswordResetEmail(email: String): Flow<State<Nothing>> = callbackFlow {
        trySend(State.Loading)
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                trySend(State.Success)
            }.addOnFailureListener{
                trySend(State.Error(it.message!!))
            }
        awaitClose()
    }
    override fun getSignedInUser() = auth.currentUser
    override fun authenticateWithGoogle(account: GoogleSignInAccount): Flow<State<FirebaseUser>> = callbackFlow{
        trySend(State.Loading)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential).addOnSuccessListener {
            trySend(State.SuccessWithData(it.user!!))
        }.addOnFailureListener {
            trySend(State.Error(it.message!!))
        }
        awaitClose()
    }
}

