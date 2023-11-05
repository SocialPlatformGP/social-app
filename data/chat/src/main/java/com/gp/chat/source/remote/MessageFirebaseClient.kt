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
import com.gp.chat.model.Message
import com.gp.chat.model.NetworkMessage
import com.gp.chat.util.ChatMapper.toMessage
import com.gp.socialapp.utils.State
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MessageFirebaseClient : MessageRemoteDataSource{

    val databaseReference = Firebase.database.reference

    override suspend fun  sendMessage( chatId: String, message: NetworkMessage): String {
        var id = chatId
        if (id.isEmpty()) {
            id=databaseReference.child("messages").push().key!!
        }
        databaseReference.child("messages").child(id).push().setValue(message)
            .addOnSuccessListener {
            Log.d("MessageFirebaseClient", "Message sent successfully")
            }
            .addOnFailureListener { e ->

                Log.e("MessageFirebaseClient", "Error sending message", e)
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
                val messages = snapshot.children.mapNotNull { it.getValue(NetworkMessage::class.java)?.toMessage(it.key!!,snapshot.key!!) }
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


}