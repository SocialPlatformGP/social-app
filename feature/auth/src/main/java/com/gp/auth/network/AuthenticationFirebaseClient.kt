package com.gp.auth.network

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
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
}
//}
//enum class FirebaseError(val code: String, val message: String) {
//    ERROR_INVALID_CUSTOM_TOKEN("ERROR_INVALID_CUSTOM_TOKEN", "The custom token format is incorrect. Please check the documentation."),
//    ERROR_CUSTOM_TOKEN_MISMATCH("ERROR_CUSTOM_TOKEN_MISMATCH", "The custom token corresponds to a different audience."),
//    ERROR_INVALID_CREDENTIAL("ERROR_INVALID_CREDENTIAL", "The supplied auth credential is malformed or has expired."),
//    ERROR_INVALID_EMAIL("ERROR_INVALID_EMAIL", "The email address is badly formatted."),
//    ERROR_WRONG_PASSWORD("ERROR_WRONG_PASSWORD", "The password is invalid or the user does not have a password."),
//    ERROR_USER_MISMATCH("ERROR_USER_MISMATCH", "The supplied credentials do not correspond to the previously signed in user."),
//    ERROR_REQUIRES_RECENT_LOGIN("ERROR_REQUIRES_RECENT_LOGIN", "This operation is sensitive and requires recent authentication. Log in again before retrying this request."),
//    ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL", "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address."),
//    ERROR_EMAIL_ALREADY_IN_USE("ERROR_EMAIL_ALREADY_IN_USE", "The email address is already in use by another account."),
//    ERROR_CREDENTIAL_ALREADY_IN_USE("ERROR_CREDENTIAL_ALREADY_IN_USE", "This credential is already associated with a different user account."),
//    ERROR_USER_DISABLED("ERROR_USER_DISABLED", "The user account has been disabled by an administrator."),
//    ERROR_USER_TOKEN_EXPIRED("ERROR_USER_TOKEN_EXPIRED", "The user's credential is no longer valid. The user must sign in again."),
//    ERROR_USER_NOT_FOUND("ERROR_USER_NOT_FOUND", "There is no user record corresponding to this identifier. The user may have been deleted."),
//    ERROR_INVALID_USER_TOKEN("ERROR_INVALID_USER_TOKEN", "The user's credential is no longer valid. The user must sign in again."),
//    ERROR_OPERATION_NOT_ALLOWED("ERROR_OPERATION_NOT_ALLOWED", "This operation is not allowed. You must enable this service in the console."),
//    ERROR_WEAK_PASSWORD("ERROR_WEAK_PASSWORD", "The given password is invalid."),
//    ERROR_MISSING_EMAIL("ERROR_MISSING_EMAIL", "An email address must be provided")
//}