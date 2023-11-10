package com.gp.users.Source.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.gp.socialapp.database.model.UserEntity
import com.gp.socialapp.utils.State
import com.gp.users.model.NetworkUser
import com.gp.users.model.User
import com.gp.users.util.UserMapper.toModel
import com.gp.users.util.UserMapper.toNetworkModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserfirestoreClient @Inject constructor(val firestore: FirebaseFirestore) :
    UserRemoteDataSource {
    override fun createUser(user: NetworkUser) = callbackFlow {
        trySend(State.Loading)
        firestore.collection("users").add(user).addOnSuccessListener {
            trySend(State.Success)
            Log.d("TAG", "User added Successfully")
        }.addOnFailureListener {
            trySend(State.Error("User Creation Failed: ${it.message}"))
            Log.d("TAG", "User addition Failed")
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

    override suspend fun fetchAllUsers() = callbackFlow {
        val listener = firestore.collection("users").addSnapshotListener { data, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (data != null) {
                val result = mutableListOf<UserEntity>()
                for (document in data.documents) {
                    result.add(document.toObject(UserEntity::class.java)!!)
                }
                trySend(result)
            }
        }
        awaitClose { listener.remove() }
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
        val listener=ref.addSnapshotListener { data, error ->
            if (error!=null){
                close(error)
                return@addSnapshotListener
            }
            if (data!=null){
                val result = mutableListOf<User>()
                for (document in data.documents) {

                    result.add(document.toObject(NetworkUser::class.java)!!.toModel())
                }
                trySend(State.SuccessWithData(result))
            }
        }
        awaitClose { listener.remove() }
    }
}