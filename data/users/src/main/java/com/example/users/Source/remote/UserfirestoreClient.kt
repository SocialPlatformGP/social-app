package com.example.users.Source.remote

import android.util.Log
import com.example.users.model.NetworkUser
import com.example.users.util.UserMapper.toNetworkModel
import com.google.firebase.firestore.FirebaseFirestore
import com.gp.socialapp.database.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class UserfirestoreClient @Inject constructor(val firestore:FirebaseFirestore):UserRemoteDataSource {
    override suspend fun createPost(networkUser: NetworkUser) {
        firestore.collection("Users").add(networkUser).addOnSuccessListener {
            Log.d("TAG", "User added Successfully")
        }
            .addOnFailureListener {
                Log.d("TAG", "User addition Failed")
            }
    }

    override suspend fun updatePost(user: UserEntity) {
        firestore.collection("posts").document(user.userID)
            .set(user.toNetworkModel()).addOnSuccessListener {
                Log.d("TAG", "Post Updated Successfully")
            }.addOnFailureListener {
                Log.d("TAG", "Post Update Failed")
            }

    }

    override suspend fun fetchPosts(): Flow<List<NetworkUser>> {
        val result = mutableListOf<NetworkUser>()
        firestore
            .collection("Users")
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        result.add(document.toObject(NetworkUser::class.java))
                    }
                }
            }
        return flowOf(result)
    }

    override suspend fun deletePost(user: UserEntity) {
        firestore
            .collection("Users")
            .document(user.userID + "-" + user.userName)
            .delete()
            .addOnSuccessListener {
                Log.d("TAG", "User Deleted Successfully")
            }.addOnFailureListener {
                Log.d("TAG", "User Deletion Failed")
            }
    }
}