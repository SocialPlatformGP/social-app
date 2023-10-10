package com.gp.socialapp.source.remote

import android.util.Log
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.util.PostMapper.toEntity
import com.gp.socialapp.util.PostMapper.toNetworkModel
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class PostFirestoreClient@Inject constructor(private val firestore: FirebaseFirestore): PostRemoteDataSource {
    override suspend fun createPost(post: NetworkPost){
        firestore.collection("posts").add(post).addOnSuccessListener {
            Log.d("TAG", "Post Created Successfully")
        }.addOnFailureListener {
            Log.d("TAG", "Post Creation Failed")
        }
    }

    override suspend fun fetchPosts(): List<PostEntity> {
        return suspendCoroutine { continuation ->
            Log.d("PostRepositoryImpl", "entered: ")
            firestore.collection("posts").get()
                .addOnSuccessListener { documents ->
                    if(documents != null) {
                        val result = mutableListOf<PostEntity>()
                        for (document in documents) {
                            result.add(document.toObject(NetworkPost::class.java).toEntity(document.id))
                        }
                        continuation.resumeWith(Result.success(result))
                    }
                }.addOnFailureListener {
                    continuation.resumeWith(Result.failure(it))
                }
        }

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