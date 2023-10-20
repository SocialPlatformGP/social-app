package com.gp.socialapp.source.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Reply
import com.gp.socialapp.util.ReplyMapper.toEntity
import com.gp.socialapp.util.ReplyMapper.toNetworkModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ReplyFirestoreClient @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReplyRemoteDataSource {
    private val ref = firestore.collection("replies")


    override suspend fun createReply(reply: NetworkReply) {
        firestore
            .collection("replies")
            .add(reply)
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
                val result = mutableListOf<ReplyEntity>()
                for (document in data.documents) {
                    Log.d("bind2", "${document.id} => ${document.data}")
                    result.add(document.toObject(NetworkReply::class.java)!!.toEntity(document.id))
                }
                trySend(result)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updateReplyRemote(reply:ReplyEntity) {
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
            .document(reply.postId + "-" + reply.path.joinToString(separator = "-"))
            .delete()
            .addOnSuccessListener {
                Log.d("TAG", "Post Deleted Successfully")
            }.addOnFailureListener {
                Log.d("TAG", "Post Deletion Failed")
            }
    }

    override suspend fun upVoteReply(reply: ReplyEntity) {
        ref.document(reply.id).get().addOnSuccessListener {
            val upvotes = it.getLong("upvotes")!!
            ref.document(reply.id).update("upvotes",upvotes+1)
        }
    }

    override suspend fun downVoteReply(reply: ReplyEntity) {
        ref.document(reply.id).get().addOnSuccessListener {
            val upvotes = it.getLong("upvotes")!!
            ref.document(reply.id).update("upvotes",upvotes-1)
        }
    }
}