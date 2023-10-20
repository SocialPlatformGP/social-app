package com.gp.socialapp.source.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.util.PostMapper.toEntity
import com.gp.socialapp.util.PostMapper.toNetworkModel
import com.gp.socialapp.utils.State
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PostFirestoreClient@Inject constructor(private val firestore: FirebaseFirestore): PostRemoteDataSource {
    private val ref = firestore.collection("posts")
    override fun createPost(post: NetworkPost) = callbackFlow<State<Nothing>> {
        trySend(State.Loading)
        firestore.collection("posts").add(post).addOnSuccessListener {
            trySend(State.Success)
        }.addOnFailureListener {
            trySend(State.Error("Post Creation Failed: ${it.message}"))
        }
        awaitClose()
    }


    override fun fetchPosts(): Flow<List<PostEntity>> = callbackFlow {
        val listener=ref.addSnapshotListener { data, error ->
            if (error!=null){
                close(error)
                return@addSnapshotListener
            }
            if (data!=null){
                val result = mutableListOf<PostEntity>()
                for (document in data.documents) {
                    result.add(document.toObject(NetworkPost::class.java)!!.toEntity(document.id))
                }
                trySend(result)
            }
        }
        awaitClose { listener.remove() }

    }

    override suspend fun updatePost(post: PostEntity) {
        firestore.collection("posts").document(post.id)
            .set(post.toNetworkModel()).addOnSuccessListener {
                Log.d("TAG", "Post Updated Successfully")
            }.addOnFailureListener {
                Log.d("TAG", "Post Update Failed")
            }
    }

    override suspend fun deletePost(post: PostEntity) {
        firestore.collection("posts").document(post.id)
            .delete().addOnSuccessListener {
                Log.d("TAG", "Post Deleted Successfully")
            }.addOnFailureListener {
                Log.d("TAG", "Post Deletion Failed")
            }
    }
}