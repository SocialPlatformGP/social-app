package com.gp.auth.network

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.gp.auth.R
import com.gp.socialapp.utils.State
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
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
                                "LocalizedMessage: ${(it as FirebaseException).localizedMessage}, " +
                                "Stacktrace: ${(it as FirebaseException).stackTrace}, " +
                                "Message: ${(it as FirebaseException).message}"
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