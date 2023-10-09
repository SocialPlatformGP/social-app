package com.gp.socialapp.source.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.gp.socialapp.model.Reply
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ReplyFirestoreClient @Inject constructor(private val firestore: FirebaseFirestore) :
    ReplyRemoteDataSource {

    override suspend fun createReply(reply: Reply) {
        firestore
            .collection("replies")
            .document(reply.postId + "-" + reply.path.joinToString(separator = "-"))
            .set(reply)
            .addOnSuccessListener {
                Log.d("TAG", "reply Created Successfully")
            }
            .addOnFailureListener {
                Log.d("TAG", "reply Creation Failed")
            }
    }

    override  fun fetchReplies(postId: String): Flow<List<Reply>> {
        val result = mutableListOf<Reply>()
        firestore
            .collection("replies")
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        result.add(document.toObject(Reply::class.java))
                    }
                }
            }
        return flowOf(result)
    }

    override suspend fun updateReply(reply:Reply) {
        firestore.
        collection("replies")
            .document(reply.postId + "-" + reply.path.joinToString(separator = "-"))
            .set(reply)
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
}