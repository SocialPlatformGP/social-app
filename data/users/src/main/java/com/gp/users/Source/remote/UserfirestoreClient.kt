package com.gp.users.Source.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.gp.socialapp.database.model.UserEntity
import com.gp.socialapp.utils.State
import com.gp.users.model.NetworkUser
import com.gp.users.util.UserMapper.toNetworkModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserfirestoreClient @Inject constructor(val firestore:FirebaseFirestore):
    UserRemoteDataSource {
    override fun createUser(user: NetworkUser) = callbackFlow{
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

    override fun updateUser(user: UserEntity)= callbackFlow<State<Nothing>>{
        trySend(State.Loading)

        val querySnapshot = firestore.collection("users")
            .whereEqualTo("userEmail", user.userEmail)
            .get()
            .await()

        if (querySnapshot.isEmpty) {
            trySend(State.Error("User not found with email: ${user.userEmail}"))
            return@callbackFlow
        }
        val documentRef =  querySnapshot.documents.first().reference
        documentRef.set(user.toNetworkModel())
            .addOnSuccessListener {
                trySend(State.Success)
                Log.d("TAG", "User Updated Successfully")
            }.addOnFailureListener { exception ->
                trySend(State.Error("User update failed: ${exception?.message}"))
                Log.d("TAG", "User Update Failed")
            }
        awaitClose()
    }

    override fun deleteUser(user: UserEntity)= callbackFlow<State<Nothing>>{
        trySend(State.Loading)
        val querySnapshot = firestore.collection("users")
            .whereEqualTo("userEmail", user.userEmail)
            .get()
            .await()
        if (querySnapshot.isEmpty) {
            trySend(State.Error("User not found with email: ${user.userEmail}"))
            return@callbackFlow
        }
        val documentRef =  querySnapshot.documents.first().reference
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
}