package com.gp.socialapp.source.remote

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Post
import com.gp.socialapp.util.PostMapper.toModel
import com.gp.socialapp.util.PostMapper.toNetworkModel
import com.gp.socialapp.utils.State
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PostFirestoreClient@Inject constructor(private val firestore: FirebaseFirestore): PostRemoteDataSource {
    private val ref = firestore.collection("posts")
    private val currentEmail = Firebase.auth.currentUser?.email
    override fun createPost(post: Post) = callbackFlow<State<Nothing>> {
        trySend(State.Loading)
        firestore.collection("posts").add(post.toNetworkModel()).addOnSuccessListener {
            trySend(State.Success)
        }.addOnFailureListener {
            trySend(State.Error("Post Creation Failed: ${it.message}"))
        }
        awaitClose()
    }


    override fun fetchPosts(): Flow<List<Post>> = callbackFlow {
        val listener=ref.addSnapshotListener { data, error ->
            if (error!=null){
                close(error)
                return@addSnapshotListener
            }
            if (data!=null){
                val result = mutableListOf<Post>()
                for (document in data.documents) {
                    result.add(document.toObject(NetworkPost::class.java)!!.toModel(document.id))
                }
                trySend(result)
            }
        }
        awaitClose { listener.remove() }
    }
    override fun fetchPostById(id: String): Flow<Post> = callbackFlow {
        val listener=ref.document(id).addSnapshotListener { data, error ->
            if (error!=null){
                close(error)
                return@addSnapshotListener
            }
            if (data!=null){
                trySend(data.toObject(NetworkPost::class.java)!!.toModel(data.id))
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updatePost(post: Post) {
        firestore.collection("posts").document(post.id)
            .set(post.toNetworkModel()).addOnSuccessListener {
                Log.d("TAG", "Post Updated Successfully")
            }.addOnFailureListener {
                Log.d("TAG", "Post Update Failed")
            }
    }

    override suspend fun deletePost(post: Post) {
        firestore.collection("posts").document(post.id)
            .delete().addOnSuccessListener {
                Log.d("TAG", "Post Deleted Successfully")
            }.addOnFailureListener {
                Log.d("TAG", "Post Deletion Failed")
            }
    }


    override suspend fun upVotePost(post: Post) {
        ref.document(post.id).get().addOnSuccessListener {
            val remotePost = it.toObject(NetworkReply::class.java)!!
            if (currentEmail in remotePost.upvoted && currentEmail !in remotePost.downvoted) {
                ref.document(post.id).update("votes", remotePost.votes - 1)
                ref.document(post.id).update("upvoted", remotePost.upvoted - currentEmail!!)
            } else if (currentEmail in remotePost.downvoted && currentEmail !in remotePost.upvoted) {
                ref.document(post.id).update("votes", remotePost.votes + 2)
                ref.document(post.id).update("upvoted", remotePost.upvoted + currentEmail!!)
                ref.document(post.id).update("downvoted", remotePost.downvoted - currentEmail!!)
            } else if (currentEmail !in remotePost.upvoted && currentEmail !in remotePost.downvoted) {
                ref.document(post.id).update("votes", remotePost.votes + 1)
                ref.document(post.id).update("upvoted", remotePost.upvoted + currentEmail!!)
            }

        }
    }


    override suspend fun downVotePost(post: Post) {
        ref.document(post.id).get().addOnSuccessListener {
            val remotePost = it.toObject(NetworkPost::class.java)!!
            if (currentEmail in remotePost.downvoted && currentEmail !in remotePost.upvoted) {
                ref.document(post.id).update("votes", remotePost.votes + 1)
                ref.document(post.id).update("downvoted", remotePost.downvoted - currentEmail!!)
            } else if (currentEmail in remotePost.upvoted && currentEmail !in remotePost.downvoted) {
                ref.document(post.id).update("votes", remotePost.votes - 2)
                ref.document(post.id).update("downvoted", remotePost.downvoted + currentEmail!!)
                ref.document(post.id).update("upvoted", remotePost.upvoted - currentEmail!!)
            } else if (currentEmail !in remotePost.upvoted && currentEmail !in remotePost.downvoted) {
                ref.document(post.id).update("votes", remotePost.votes - 1)
                ref.document(post.id).update("downvoted", remotePost.downvoted + currentEmail!!)
            }

        }
    }
}