package com.gp.chat.source.remote

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.gp.chat.model.ChatGroup
import com.gp.chat.model.ChatUser
import com.gp.chat.model.Message
import com.gp.chat.model.NetworkChatGroup
import com.gp.chat.model.NetworkChatUser
import com.gp.chat.model.NetworkMessage
import com.gp.chat.model.NetworkRecentChat
import com.gp.chat.model.PrivateChats
import com.gp.chat.model.PrivateChatsNetwork
import com.gp.chat.model.RecentChat
import com.gp.chat.util.ChatMapper.toChatUser
import com.gp.chat.util.ChatMapper.toMap
import com.gp.chat.util.ChatMapper.toModel
import com.gp.chat.util.ChatMapper.toNetworkChatGroup
import com.gp.chat.util.ChatMapper.toNetworkMessage
import com.gp.chat.util.ChatMapper.toNetworkPrivateChats
import com.gp.chat.util.ChatMapper.toNetworkRecentChat
import com.gp.chat.util.ChatMapper.toRecentChat
import com.gp.socialapp.utils.State
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MessageFirebaseClient(
    private val database: FirebaseDatabase
) : MessageRemoteDataSource {

    val CHAT = "chats"
    val RECENT_CHATS = "recentChats"
    val CHAT_USER = "chatUser"
    private val PRIVATE_CHAT = "privateChat"
    val MESSAGES = "messages"
    val TAG = "MessageFirebaseClient"
    val RECEIVER_USER = "receiverUsers"
    val GROUP = "group"


    override fun insertChat(chat: ChatGroup): Flow<State<String>> = callbackFlow {
        val ref = database.reference.child(CHAT).push()
        val chat=chat.copy(id = ref.key!!,name = "private")
        ref.setValue(chat)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "onDataChange: $snapshot")
                    trySend(State.SuccessWithData(ref.key!!))
                } else {
                    trySend(State.Error("error"))
                }
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(State.Error(error.message))
            }
        }
        ref.addValueEventListener(listener)
        awaitClose {
            ref.removeEventListener(listener)
        }
    }

    override fun insertRecentChat(recentChat: RecentChat, chatId: String): Flow<State<String>> =
        callbackFlow {
            val ref = database.reference.child(RECENT_CHATS).child(chatId).push()
            ref.setValue(recentChat)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.d(TAG, "onDataChange: $snapshot")
                        trySend(State.SuccessWithData(ref.key!!))
                    } else {
                        trySend(State.Error("error"))
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    trySend(State.Error(error.message))
                }
            }
            ref.addValueEventListener(listener)
            awaitClose {
                ref.removeEventListener(listener)
            }
        }

    override fun sendMessage(message: Message): Flow<State<String>> = callbackFlow {
        database.reference.child(MESSAGES).child(message.groupId).push().setValue(message.toNetworkMessage())
            .addOnSuccessListener {
                trySend(State.SuccessWithData("success"))
            }
            .addOnFailureListener {
                trySend(State.Error(it.message!!))
            }
        awaitClose ()


    }

override fun getMessages(chatId: String): Flow<State<List<Message>>> = callbackFlow {
    val ref = database.reference.child("messages").child(chatId)
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val messages = mutableListOf<Message>()

            for (messageSnapshot in snapshot.children) {
                val networkMessage = messageSnapshot.getValue<NetworkMessage>()

                if (networkMessage != null) {
                    val message = networkMessage.toModel(messageSnapshot.key!!)
                    messages.add(message)
                }
            }

            if (messages.isNotEmpty()) {
                trySend(State.SuccessWithData(messages))
            } else {
                trySend(State.Error("No messages found"))
            }
        }

        override fun onCancelled(error: DatabaseError) {
            trySend(State.Error(error.message))
        }
    }

    ref.addValueEventListener(listener)

    awaitClose {
        ref.removeEventListener(listener)
    }
}



    override fun getRecentChats(chatsId: List<String>): Flow<State<List<RecentChat>>> =
        callbackFlow {
            val ref = database.reference.child(RECENT_CHATS)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val list = mutableListOf<RecentChat>()
                        for(chatId in chatsId){

                            val recentChats = snapshot.child(chatId).
                            getValue<NetworkRecentChat>()?.toRecentChat(snapshot.key!!)
                            if(recentChats!=null){
                                list.add(recentChats)
                            }
                        }
                        trySend(State.SuccessWithData(list))
                    } else {
                        trySend(State.Error("error"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(State.Error(error.message))
                }
            }
            ref.addValueEventListener(listener)
            awaitClose {
                ref.removeEventListener(listener)
            }

        }


    override fun insertChatToUser(chatId: String, userEmail: String,receiverEmail:String): Flow<State<String>> =
        callbackFlow {
                database.reference.child(CHAT_USER).child(userEmail).child(GROUP).child(chatId)
                    .setValue(true)
                    database.reference.child(CHAT_USER).child(receiverEmail).child(GROUP).child(chatId)
                    .setValue(true)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            trySend(State.Success)
                        } else {
                            trySend(State.Error(it.exception?.message!!))
                        }
                    }
            awaitClose()
        }

    override fun getUserChats(userEmail: String): Flow<State<ChatUser>> = callbackFlow {
        val ref = database.reference.child(CHAT_USER).child(userEmail).child(GROUP)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val chats = snapshot.children.map {
                        it.key
                    }
                    val newChats :Map<String,Boolean> = chats.associate { it ->
                        it!! to true
                    }
                    trySend(State.SuccessWithData(ChatUser(groups = newChats)))
                } else {
                    trySend(State.Error("error"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(State.Error(error.message))
            }
        }
        ref.addValueEventListener(listener)
        awaitClose {
            ref.removeEventListener(listener)
        }
    }

    override fun insertPrivateChat(
        sender: String,
        receiver: String,
        chatId: String
    ): Flow<State<String>> = callbackFlow {
        database.reference.child(PRIVATE_CHAT).child(sender).child(RECEIVER_USER)
            .child(receiver).setValue(chatId)
             database.reference.child(PRIVATE_CHAT).child(receiver).child(RECEIVER_USER)
            .child(sender).setValue(chatId)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    trySend(State.SuccessWithData(chatId))
                } else {
                    trySend(State.Error(it.exception?.message!!))
                }
            }
        awaitClose()

    }

    override fun haveChatWithUser(userEmail: String, otherUserEmail: String): Flow<State<String>> =
        callbackFlow {
            val ref = database.reference.child(PRIVATE_CHAT).child(userEmail).child(RECEIVER_USER).child(otherUserEmail)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("mohamed21", "onDataChange: ${snapshot.key }55${ snapshot.value}")
                    if (snapshot.exists()) {
                        Log.d("mohamed22", "onDataChange: ${snapshot.key }55${ snapshot.value}")

                        trySend(State.SuccessWithData(snapshot.value.toString()))
                    }else{
                        trySend(State.SuccessWithData("-1"))
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: ${error.message}")
                    trySend(State.Error(error.message))
                }

            }
            ref.addListenerForSingleValueEvent(listener)
            awaitClose {
                ref.removeEventListener(listener)
            }
        }

    override fun updateRecentChat(recentChat: RecentChat, chatId: String): Flow<State<String>> =
        callbackFlow {
            database.reference.child(RECENT_CHATS).child(chatId).updateChildren(
                recentChat.toMap()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    trySend(State.SuccessWithData(chatId))
                } else {
                    trySend(State.Error(it.exception?.message!!))
                }
            }

awaitClose()

        }


}