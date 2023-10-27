package com.gp.socialapp.source.remote

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Reply
import com.gp.socialapp.util.ReplyMapper.toEntity
import com.gp.socialapp.util.ReplyMapper.toModel
import com.gp.socialapp.util.ReplyMapper.toNetworkModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReplyFirestoreClient @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReplyRemoteDataSource {
    private val ref = firestore.collection("replies")
    private val currentEmail = Firebase.auth.currentUser?.email


    override suspend fun createReply(reply: Reply) {
        firestore
            .collection("replies")
            .add(reply.toNetworkModel())
            .addOnSuccessListener {
                Log.d("TAG", "reply Created Successfully")
            }
            .addOnFailureListener {
                Log.d("TAG", "reply Creation Failed")
            }
    }

    override  fun fetchReplies(postId: String) = callbackFlow{

        val listener =ref.whereEqualTo("postId",postId).addSnapshotListener{data,error->
            if(error!=null){
                close(error)
                return@addSnapshotListener
            }
            if(data!=null){
                val result = mutableListOf<Reply>()
                for (document in data.documents) {
                    Log.d("bind2", "${document.id} => ${document.data}")
                    result.add(document.toObject(NetworkReply::class.java)!!.toModel(document.id))
                }
                trySend(result)
            }
        }
        awaitClose { listener.remove() }
    }
    override  suspend fun getReplyCountByPostId(postId: String):Int {
        val result = try {
            ref.whereEqualTo("postId",postId).get().await().size()
        }catch (e:Exception){
            Log.d("TAG  ERROR", "getReplyCountByPostId: ${e.message}")
            -1
        }
        return result
    }

    override suspend fun updateReplyRemote(reply:Reply) {
        firestore.
        collection("replies")
            .document(reply.id)
            .set(reply.toNetworkModel())
            .addOnSuccessListener {
                Log.d("TAG", "Reply Updated Successfully")
            }.addOnFailureListener {
                Log.d("TAG", "Reply Update Failed")
            }
    }

    override suspend fun deleteReply(reply: Reply) {
        firestore
            .collection("replies")
            .document(reply.id)
            .delete()
            .addOnSuccessListener {
                Log.d("TAG", "Post Deleted Successfully")
            }.addOnFailureListener {
                Log.d("TAG", "Post Deletion Failed")
            }
    }

    override suspend fun upVoteReply(reply: Reply) {
        ref.document(reply.id).get().addOnSuccessListener {
            val remoteReply = it.toObject(NetworkReply::class.java)!!
            if (currentEmail in remoteReply.upvoted && currentEmail !in remoteReply.downvoted) {
                ref.document(reply.id).update("votes", remoteReply.votes - 1)
                ref.document(reply.id).update("upvoted", remoteReply.upvoted - currentEmail!!)
            } else if (currentEmail in remoteReply.downvoted && currentEmail !in remoteReply.upvoted) {
                ref.document(reply.id).update("votes", remoteReply.votes + 2)
                ref.document(reply.id).update("upvoted", remoteReply.upvoted + currentEmail!!)
                ref.document(reply.id).update("downvoted", remoteReply.downvoted - currentEmail)
            } else if (currentEmail !in remoteReply.upvoted && currentEmail !in remoteReply.downvoted) {
                ref.document(reply.id).update("votes", remoteReply.votes + 1)
                ref.document(reply.id).update("upvoted", remoteReply.upvoted + currentEmail!!)
            }

            }
        }


    override suspend fun downVoteReply(reply: Reply) {
        ref.document(reply.id).get().addOnSuccessListener {
            val remoteReply = it.toObject(NetworkReply::class.java)!!
            if (currentEmail in remoteReply.downvoted && currentEmail !in remoteReply.upvoted) {
                ref.document(reply.id).update("votes", remoteReply.votes + 1)
                ref.document(reply.id).update("downvoted", remoteReply.downvoted - currentEmail!!)
            } else if (currentEmail in remoteReply.upvoted && currentEmail !in remoteReply.downvoted) {
                ref.document(reply.id).update("votes", remoteReply.votes - 2)
                ref.document(reply.id).update("downvoted", remoteReply.downvoted + currentEmail!!)
                ref.document(reply.id).update("upvoted", remoteReply.upvoted - currentEmail)
            } else if (currentEmail !in remoteReply.upvoted && currentEmail !in remoteReply.downvoted) {
                ref.document(reply.id).update("votes", remoteReply.votes - 1)
                ref.document(reply.id).update("downvoted", remoteReply.downvoted + currentEmail!!)
            }

        }
    }
}