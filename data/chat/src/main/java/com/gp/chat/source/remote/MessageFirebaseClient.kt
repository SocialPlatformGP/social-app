package com.gp.chat.source.remote


import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.ChatGroup
import com.gp.chat.model.ChatUser
import com.gp.chat.model.Message
import com.gp.chat.model.NetworkChatGroup
import com.gp.chat.model.NetworkMessage
import com.gp.chat.model.NetworkRecentChat
import com.gp.chat.model.RecentChat
import com.gp.chat.util.ChatMapper.toMap
import com.gp.chat.util.ChatMapper.toModel
import com.gp.chat.util.ChatMapper.toNetworkMessage
import com.gp.chat.util.ChatMapper.toRecentChat
import com.gp.chat.util.RemoveSpecialChar
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
import com.gp.chat.util.RemoveSpecialChar.restoreOriginalEmail
import com.gp.socialapp.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MessageFirebaseClient(
    private val database: FirebaseDatabase
) : MessageRemoteDataSource {

    private val CHAT = "chats"
    private val RECENT_CHATS = "recentChats"
    private val CHAT_USER = "chatUsers"
    private val PRIVATE_CHAT = "privateChats"
    private val MESSAGES = "messages"
    private val TAG = "MessageFirebaseClient"
    private val RECEIVER_USER = "receiverUsers"
    private val GROUP = "groups"

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
    val ref = database.reference.child(MESSAGES).child(chatId)
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val messages = mutableListOf<Message>()
            for (messageSnapshot in snapshot.children) {
                val networkMessage = messageSnapshot.getValue(NetworkMessage::class.java)
                if (networkMessage != null) {
                    val message = networkMessage.toModel(messageSnapshot.key!!,chatId).copy(
                        senderId = restoreOriginalEmail(networkMessage.senderId)
                    )
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

                            val recentChats = (snapshot.child(chatId).getValue(NetworkRecentChat::class.java)?.toRecentChat(chatId))
                            Log.d("testo", "onDataChange1: ${snapshot.child(chatId)}")
                            Log.d("testo", "onDataChange2: ${snapshot.child(chatId).getValue(NetworkRecentChat::class.java)}")

                            Log.d("testo", "onDataChange: $recentChats")
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

    override fun deleteMessage(messageId: String, chatId: String) {
        val messageReference = database.reference.child(MESSAGES).child(chatId).child(messageId)
        Log.d("deleteFun", "mI: $messageId cI:  $chatId")
        messageReference.removeValue()

            .addOnSuccessListener {
                println("Message deleted successfully!")
            }
            .addOnFailureListener {
                println("Failed to delete message: ${it.message}")
            }

    }

    override fun updateMessage(messageId: String, chatId: String, updatedText: String) {
        val messageReference = database.reference.child(MESSAGES)
        messageReference.child(chatId).child(messageId).child("message").setValue(updatedText)
            .addOnSuccessListener {
                println("Message updated successfully!")
            }
            .addOnFailureListener {
                println("Failed to update message: ${it.message}")
            }
    }

    override fun leaveGroup(chatId: String) {
        val email = Firebase.auth.currentUser?.email!!
        val userEmail= removeSpecialCharacters(email)
        Log.d("logF",  email)
        removeUserFromGroup(chatId)
        val usersReference=database.reference.child("chats").child(chatId).child("members")
        usersReference.child(userEmail).removeValue().addOnSuccessListener {
                println("Left the group successfully!")
            }
            .addOnFailureListener {
                println("Failed to leave the group: ${it.message}")
            }
    }


     private fun removeUserFromGroup(groupId: String) {
        val email = Firebase.auth.currentUser?.email!!
        val userEmail= removeSpecialCharacters(email)
        database.reference.child("chatUsers")
            .child(userEmail).child(GROUP).child(groupId).removeValue()
            .addOnSuccessListener {
                println("Removed user from the group successfully!")
            }
            .addOnFailureListener {
                println("Failed to remove user from the group: ${it.message}")
            }
    }

    override fun fetchGroupMessages(groupId: String): Flow<List<Message>> = callbackFlow {
        val messagesReference = database.reference.child(MESSAGES).child(groupId)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { (it.getValue(NetworkMessage::class.java))?.toModel(it.key!!,groupId) }
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
        database.reference.child(MESSAGES)
            .child(message.groupId)
            .push().setValue(message.toNetworkMessage())
            .addOnSuccessListener {
                Log.d("EDREES", "Message Sent")
                val updates = HashMap<String, Any>()
                updates["lastMessage"] = recentChat.lastMessage
                updates["timestamp"] = recentChat.timestamp
                database.reference.child(RECENT_CHATS)
                    .child(message.groupId)
                    .updateChildren(updates)
                    .addOnSuccessListener {
                        Log.d("EDREES", "Recent Sent")
                        trySend(State.Success)
                    }.addOnFailureListener{
                        trySend(State.Error(it.localizedMessage!!))
                    }
            }.addOnFailureListener {
                trySend(State.Error(it.localizedMessage!!))
            }
        awaitClose()
    }

    override fun createGroupChat(group: NetworkChatGroup, recentChat: NetworkRecentChat): Flow<State<String>> = callbackFlow{
        Log.d("EDREES", "Before Sending")
        try {
            val chatRef = database.reference.child(CHAT).push()
            val chatKey = chatRef.key
            if (chatKey == null) {
                trySend(State.Error("Failed to generate a chat key"))
                return@callbackFlow
            }
            chatRef.setValue(group)
                .addOnSuccessListener {
                    database.reference.child(RECENT_CHATS).child(chatKey)
                        .setValue(recentChat)
                        .addOnSuccessListener {
                            val userGroupData = hashMapOf<String, Any>()
                            for (user in group.members.entries) {
                                userGroupData["$CHAT_USER/${RemoveSpecialChar.removeSpecialCharacters(user.key)}/$GROUP/${chatKey}"] = user.value
                            }
                            val updateResult = database.reference.updateChildren(userGroupData).addOnSuccessListener {
                                trySend(State.SuccessWithData(chatKey))
                            }.addOnFailureListener {
                                trySend(State.Error("Failed to update user groups"))
                            }
                        }
                        .addOnFailureListener {
                            trySend(State.Error(it.localizedMessage ?: "Failed to create recent chat"))
                        }
                }
                .addOnFailureListener {
                    trySend(State.Error(it.localizedMessage ?: "Failed to create group chat"))
                }
        } catch (e: Exception) {
            trySend(State.Error(e.localizedMessage ?: "An error occurred"))
        }
        awaitClose()
    }

    override fun getGroupMembersEmails(groupId: String): Flow<State<List<String>>> = callbackFlow {
        trySend(State.Loading)
        val membersReference = database.getReference("$CHAT/$groupId/members")

        try {
            val membersSnapshot = withContext(Dispatchers.IO) {
                membersReference.get().await()
            }
            val userEmails = membersSnapshot.children.map { restoreOriginalEmail(it.key ?: "")}
            trySend(State.SuccessWithData(userEmails))
        } catch (e: Exception) {
            trySend(State.Error(e.localizedMessage ?: "An error occurred"))
        }
        awaitClose()
    }

    override fun removeMemberFromGroup(groupId: String, memberEmail: String): Flow<State<String>> = callbackFlow{
        val groupMembersRef = database.getReference("$CHAT/$groupId/members")
        val userGroupsRef = database.getReference("$CHAT_USER/${
            removeSpecialCharacters( memberEmail)}/groups")
        try {
            groupMembersRef.child(removeSpecialCharacters( memberEmail)).removeValue()
            userGroupsRef.child(groupId).removeValue()
            trySend(State.Success)
        } catch (e: Exception) {
            trySend(State.Error("Error removing user from the group: ${e.message}"))
        } finally {
            awaitClose()
        }
    }

}