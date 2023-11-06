package com.gp.chat.source.remote

import android.util.Log
import com.google.api.LogDescriptor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.NetworkMessage
import com.gp.chat.model.NetworkRecentChat
import com.gp.chat.model.RecentChat
import com.gp.chat.util.ChatMapper.toMessage
import com.gp.chat.util.ChatMapper.toRecentChat
import com.gp.socialapp.utils.State
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date

class MessageFirebaseClient : MessageRemoteDataSource {

    val databaseReference = Firebase.database.reference

    override suspend fun sendMessage(chatId: String, message: NetworkMessage): String {
        var id = chatId
        if (id =="No Chat") {
            id = databaseReference.child("messages").push().key!!
        }
        databaseReference.child("messages").child(id).push().setValue(message)
            .addOnSuccessListener {
                Log.d("MFC", "Message sent successfully")
            }
            .addOnFailureListener { e ->

                Log.e("MFC", "Error sending message", e)
            }
        return id
    }

    override fun getChatMessages(chatId: String) = callbackFlow {
        trySend(State.Loading)
        if (chatId.isEmpty()) {
            trySend(State.Error("Chat id is empty"))
            close(IllegalArgumentException("Chat id is empty"))

            return@callbackFlow
        }
        val messagesReference = databaseReference.child("messages").child(chatId)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    it.getValue(NetworkMessage::class.java)?.toMessage(it.key!!, snapshot.key!!)
                }
                trySend(State.SuccessWithData(messages))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(State.Error(error.message))
                close(error.toException())
            }
        }
        messagesReference.addValueEventListener(valueEventListener)

        awaitClose {
            messagesReference.removeEventListener(valueEventListener)
        }
    }

    override fun checkIfNewChat(userEmail: String, receiverEmail: String): Flow<State<String>> =
        callbackFlow {
            val reference = databaseReference.child("privateChats")
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val receivers =
                            snapshot.child(userEmail).child("receivers").value as? Map<*, *>
                        var chatId = receivers?.get(receiverEmail) as? String
                        if (chatId == null) {
                            trySend(State.Error("the receiver is  exist but no chat id"))
                        } else {
                            trySend(State.SuccessWithData(chatId))
                        }
                    } else {
                        trySend(State.Error("the receiver is not exist"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(State.Error(error.message))
                    close(error.toException())
                }
            }

            reference.addListenerForSingleValueEvent(listener)

            awaitClose {
                reference.removeEventListener(listener)
            }
        }

    override suspend fun updateRecent(
        chatId: String,
        message: String,
        userEmail: String,
        receiverEmail: String
    ) {
        databaseReference.child("recentChats").child(userEmail).child(chatId).setValue(
            NetworkRecentChat(
                lastMessage = message,
                timestamp = Date().toString(),
                senderName = userEmail,
                receiverName = receiverEmail,
            )
        )
        databaseReference.child("recentChats").child(receiverEmail).child(chatId).setValue(
            NetworkRecentChat(
                lastMessage = message,
                timestamp = Date().toString(),
                senderName = userEmail,
                receiverName = receiverEmail,
            )
        )
    }

    override fun getRecentChats(userEmail: String): Flow<State<List<RecentChat>>> = callbackFlow {
        Log.d("TAG", "getRecentChats: ")
        trySend(State.Loading)
        val reference = databaseReference.child("recentChats").child(userEmail)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("MessageFirebaseClient snapshot is exist22", "getRecentChats: $snapshot")
                    val chats = snapshot.children.mapNotNull {
                        it.getValue(NetworkRecentChat::class.java)?.toRecentChat(it.key!!)
                    }
                    trySend(State.SuccessWithData(chats))
                } else {
                    trySend(State.SuccessWithData(emptyList()))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(State.Error(error.message))
                close(error.toException())
            }
        }

        reference.addListenerForSingleValueEvent(listener)

        awaitClose {
            reference.removeEventListener(listener)
        }
    }

    override suspend fun createNewChat(userEmail: String, receiverEmail: String, key: String) {
        databaseReference.child("privateChats").child(userEmail).child("receivers")
            .child(receiverEmail).setValue(key)
        databaseReference.child("privateChats").child(receiverEmail).child("receivers")
            .child(userEmail).setValue(key)
            .addOnSuccessListener {
                Log.d("MFC", "createNewChat: ")
            }.addOnFailureListener { e ->
                Log.e("MFC", "createNewChat: ", e)
            }
    }

    private fun createPrivateChat(userEmail: String, receiverEmail: String, key: String) {
        val reference = databaseReference.child("privateChats").child(userEmail).child("receivers")
        reference.child(receiverEmail).setValue(key)
        databaseReference.child("privateChats").child(receiverEmail).child("receivers")
            .child(userEmail).setValue(key)
    }


}