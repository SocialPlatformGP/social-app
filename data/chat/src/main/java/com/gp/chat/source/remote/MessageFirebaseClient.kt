package com.gp.chat.source.remote

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.Message
import com.gp.chat.model.NetworkMessage
import com.gp.chat.model.RecentChat
import com.gp.chat.util.ChatMapper.toMessage
import com.gp.chat.util.ChatMapper.toNetworkMessage
import com.gp.chat.util.ChatMapper.toNetworkRecentChat
import com.gp.socialapp.utils.State
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MessageFirebaseClient : MessageRemoteDataSource{

    val databaseReference = Firebase.database.reference

    override suspend fun  sendMessage(chatId: String, message: Message) {
        databaseReference.child("Chats").child(chatId).child("Messages").push().setValue(message)
            .addOnSuccessListener {
            Log.d("MessageFirebaseClient", "Message sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e("MessageFirebaseClient", "Error sending message", e)
            }
    }

    override fun getChatMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val messagesReference = databaseReference.child("Chats").child(chatId).child("Messages")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("MessageFirebaseClient", "Messages: $snapshot")
                val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }

                trySend(messages).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        messagesReference.addValueEventListener(valueEventListener)

        awaitClose {
            messagesReference.removeEventListener(valueEventListener)
        }
    }

    override fun fetchGroupMessages(groupId: String): Flow<List<Message>> = callbackFlow {
        val messagesReference = databaseReference.child("messages").child(groupId)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { (it.getValue(NetworkMessage::class.java))?.toMessage(it.key!!, groupId) }
                trySend(messages)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        messagesReference.addValueEventListener(valueEventListener)
        awaitClose {
            messagesReference.removeEventListener(valueEventListener)
        }
    }

    override fun sendGroupMessage(message: Message, recentChat: RecentChat):Flow<State<Nothing>> = callbackFlow {
        Log.d("edrees", "Before Sending")
        trySend(State.Loading)
        databaseReference.child("messages")
            .child(message.groupId!!)
            .push().setValue(message.toNetworkMessage())
            .addOnSuccessListener {
                Log.d("EDREES", "Message Sent")
                val updates = HashMap<String, Any>()
                updates["lastMessage"] = recentChat.lastMessage!!
                updates["timestamp"] = recentChat.timestamp!!
                databaseReference.child("chats")
                    .child(message.groupId!!)
                    .updateChildren(updates)
                    .addOnSuccessListener {
                        Log.d("EDREES", "Recent Sent")
                        trySend(State.Success)
                    }.addOnFailureListener{
                        trySend(State.Error(it.localizedMessage))
                    }
            }.addOnFailureListener {
                trySend(State.Error(it.localizedMessage))
            }
        awaitClose()
    }
}