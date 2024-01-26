package com.gp.users.Source.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gp.socialapp.database.model.UserEntity
import com.gp.socialapp.utils.State
import com.gp.users.model.NetworkUser
import com.gp.users.model.User
import com.gp.users.util.UserMapper.toModel
import com.gp.users.util.UserMapper.toNetworkModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class UserfirestoreClient @Inject constructor(
    val firestore: FirebaseFirestore,
    val auth: FirebaseAuth,
    val storage: FirebaseStorage
) :
    UserRemoteDataSource {
    override fun createUser(user: NetworkUser, pfpUri: Uri): Flow<State<Nothing>> = callbackFlow {
        trySend(State.Loading)
        val imageRef = storage.reference.child("USERSPIC/${auth.currentUser?.uid ?: Date().toString()}")
        imageRef.putFile(pfpUri).addOnSuccessListener { uploadTask ->
            uploadTask.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                val newUser = user.copy(userProfilePictureURL = downloadUrl.toString())
                firestore.collection("users").add(newUser).addOnSuccessListener { documentReference ->
                    auth.currentUser?.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName("${user.userFirstName} ${user.userLastName}")
                            .setPhotoUri(downloadUrl)
                            .build()
                    )?.addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            trySend(State.Success)
                            Log.d("TAG", "User added Successfully")
                        } else {
                            trySend(State.Error("User Creation Failed: ${authTask.exception?.message}"))
                        }
                    }
                }.addOnFailureListener { firestoreException ->
                    trySend(State.Error("User Creation Failed: ${firestoreException.message}"))
                }
            }.addOnFailureListener { urlException ->
                trySend(State.Error("Image URL retrieval failed: ${urlException.message}"))
            }
        }.addOnFailureListener { uploadException ->
            trySend(State.Error("Image upload failed: ${uploadException.message}"))
        }

        awaitClose()
    }

    override fun updateUser(user: UserEntity) = callbackFlow<State<Nothing>> {
        trySend(State.Loading)

        val querySnapshot = firestore.collection("users")
            .whereEqualTo("userEmail", user.userEmail)
            .get()
            .await()

        if (querySnapshot.isEmpty) {
            trySend(State.Error("User not found with email: ${user.userEmail}"))
            return@callbackFlow
        }
        val documentRef = querySnapshot.documents.first().reference
        documentRef.set(user.toNetworkModel())
            .addOnSuccessListener {
                trySend(State.Success)
                Log.d("TAG", "User Updated Successfully")
            }.addOnFailureListener { exception ->
                trySend(State.Error("User update failed: ${exception.message}"))
                Log.d("TAG", "User Update Failed")
            }
        awaitClose()
    }

    override fun getUsersByEmails(emails: List<String>): Flow<State<List<User>>> = callbackFlow {
        trySend(State.Loading)
        try {
            val users = mutableListOf<User>()
            for (userEmail in emails) {
                Log.d("SEERDE", "getUsersByEmails: called on user: $userEmail")
                val userQuerySnapshot =
                    firestore.collection("users").whereEqualTo("userEmail", userEmail).get().await()
                if (!userQuerySnapshot.isEmpty) {
                    val user = userQuerySnapshot.first()
                        .toObject(NetworkUser::class.java)
                        .toModel()
                    Log.d("SEERDE", "getUsersByEmails: success on user $userEmail")
                    users.add(user)
                }
            }
            trySend(State.SuccessWithData(users))
        } catch (e: Exception) {
            trySend(State.Error(e.message ?: "Unknown Error"))
        }
        awaitClose()
    }

    override suspend fun fetchUser(email: String): State<NetworkUser> {
        var result =
            try {
                val data = firestore
                    .collection("users")
                    .whereEqualTo("userEmail", email).get().await()
                    .toObjects(NetworkUser::class.java)
                    .first()
                Log.d("TAG", "fetchUser: $data")
                State.SuccessWithData(data)

            } catch (e: Exception) {
                Log.d("TAG", "fetchUser: ${e.message}")
                State.Error(e.message ?: "Unknown Error")
            }
        return result
    }


    override fun deleteUser(user: UserEntity) = callbackFlow<State<Nothing>> {
        trySend(State.Loading)
        val querySnapshot = firestore.collection("users")
            .whereEqualTo("userEmail", user.userEmail)
            .get()
            .await()
        if (querySnapshot.isEmpty) {
            trySend(State.Error("User not found with email: ${user.userEmail}"))
            return@callbackFlow
        }
        val documentRef = querySnapshot.documents.first().reference
        documentRef.delete()
            .addOnSuccessListener {
                trySend(State.Success)
                Log.d("TAG", "User Deleted Successfully")
            }.addOnFailureListener {
                trySend(State.Error("User deletion failed: ${it.message}"))
                Log.d("TAG", "User Deletion Failed")
            }
        awaitClose()
    }

    override fun fetchUsers(): Flow<State<List<User>>> = callbackFlow {
        val ref = firestore.collection("users")
        val listener = ref.addSnapshotListener { data, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (data != null) {
                val result = mutableListOf<User>()
                for (document in data.documents) {
                    result.add(document.toObject(NetworkUser::class.java)!!.toModel())
                }
                trySend(State.SuccessWithData(result))
            }
        }
        awaitClose { listener.remove() }
    }

    override fun getCurrentUserEmail(): String = auth.currentUser?.email!!
}