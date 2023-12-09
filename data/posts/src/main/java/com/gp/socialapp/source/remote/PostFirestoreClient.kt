package com.gp.socialapp.source.remote

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.database.model.PostFile
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag
import com.gp.socialapp.util.PostMapper.toModel
import com.gp.socialapp.util.PostMapper.toNetworkModel
import com.gp.socialapp.utils.State
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class PostFirestoreClient@Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
): PostRemoteDataSource {
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

    override fun createPostWithFiles(post: Post, files: List<PostFile>): Flow<State<Nothing>> = callbackFlow<State<Nothing>> {
        trySend(State.Loading)
        try {
            val postKey = firestore.collection("posts").document().id
            uploadPostFiles(postKey, files).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        val updatedPost = post.copy(attachments = it.data)
                        firestore.collection("posts").document(postKey).set(updatedPost.toNetworkModel())
                            .addOnSuccessListener {
                                trySend(State.Success)
                            }.addOnFailureListener {
                            trySend(State.Error("Post Creation Failed: ${it.message}"))
                        }
                    }
                    is State.Error -> {
                        trySend(State.Error("Post Creation Failed: ${it.message}"))
                    }

                    else -> {}
                }
            }
        }catch (e: Exception){
            trySend(State.Error("Post Creation Failed: ${e.message}"))
        } finally {
            awaitClose()
        }
    }
    private fun uploadPostFiles(postKey: String, files: List<PostFile>): Flow<State<List<PostAttachment>>> = callbackFlow {
        val result = mutableListOf<PostAttachment>()
        try{
            files.forEach {file ->
                val fileName = "${UUID.randomUUID()}_${file.name}"
                val fileRef = storage.reference.child("posts/$postKey/$fileName")
                fileRef.putFile(file.uri).addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener {
                        result.add(PostAttachment(
                            url = it.toString(),
                            name = file.name,
                            type = file.type.readableType))
                        if(result.size == files.size){
                            trySend(State.SuccessWithData(result))
                        }
                    }.addOnFailureListener {
                        trySend(State.Error("Uploading File Failed: ${it.message}"))
                    }
                }.addOnFailureListener {
                    trySend(State.Error("Uploading File Failed: ${it.message}"))
                }
            }
        } catch(e: Exception) {
            trySend(State.Error("Uploading File Failed: ${e.message}"))
        } finally {
            awaitClose {  }
        }
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
    override suspend fun incrementReplyCounter(postId: String) {
        //get reply count field then increment it by 1
        val increment = FieldValue.increment(1)
        val postRef = firestore.collection("posts").document(postId)
        postRef.update("replyCount", increment)
            .addOnSuccessListener {
                // The update was successful
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }
    override suspend fun decrementReplyCounter(postId: String) {
        firestore.collection("posts").document(postId).addSnapshotListener{
                data, error ->
            if (error!=null){
                return@addSnapshotListener
            }
            if (data!=null){
                val remotePost = data.toObject(NetworkPost::class.java)!!
                firestore.collection("posts").document(postId).update("replyCount", remotePost.replyCount -1)
            }
        }
    }

    override fun getAllTags()= callbackFlow {
        val listener=firestore.collection("tags").addSnapshotListener { data, error ->
            if (error!=null){
                close(error)
                return@addSnapshotListener
            }
            if (data!=null){
                val result = mutableListOf<Tag>()
                for (document in data.documents) {
                    result.add(document.toObject(Tag::class.java)!!)
                }
                trySend(result)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun insertTag(tag: Tag) {
        firestore.collection("tags").add(tag).addOnSuccessListener {
            Log.d("TAG", "Tag Inserted Successfully")
        }.addOnFailureListener {
            Log.d("TAG", "Tag Insertion Failed")
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
                ref.document(post.id).update("downvoted", remotePost.downvoted - currentEmail)
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
                ref.document(post.id).update("upvoted", remotePost.upvoted - currentEmail)
            } else if (currentEmail !in remotePost.upvoted && currentEmail !in remotePost.downvoted) {
                ref.document(post.id).update("votes", remotePost.votes - 1)
                ref.document(post.id).update("downvoted", remotePost.downvoted + currentEmail!!)
            }

        }
    }
}