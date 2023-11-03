package com.gp.chat.source.remote

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.GroupMessage
import com.gp.chat.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

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

    override fun fetchGroupChatMessages(groupId: String): Flow<List<GroupMessage>> {
        TODO("Not yet implemented")
    }


}