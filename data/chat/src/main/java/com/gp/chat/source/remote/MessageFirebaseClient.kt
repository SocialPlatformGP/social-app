package com.gp.chat.source.remote


import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.gp.chat.model.ChatGroup
import com.gp.chat.model.ChatUser
import com.gp.chat.model.Message
import com.gp.chat.model.NetworkChatGroup
import com.gp.chat.model.NetworkMessage
import com.gp.chat.model.NetworkRecentChat
import com.gp.chat.model.RecentChat
import com.gp.chat.util.ChatMapper.toChatGroup
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
import java.util.Date
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

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
    private val currentUser = Firebase.auth.currentUser

    override fun insertChat(chat: ChatGroup): Flow<State<String>> = callbackFlow {
        val ref = database.reference.child(CHAT).push()
        val chat = chat.copy(id = ref.key!!, name = "private")
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

    override fun insertRecentChat(recentChat: RecentChat, chatId: String) {
        Log.d("zarea4", "in client start sertRecentChat: $recentChat")

        database.reference.child(RECENT_CHATS).child(chatId)
            .setValue(recentChat) { error, ref ->
                if (error == null) {
                    Log.d("zarea4", "in client sertRecentChat: $recentChat")
                } else {
                    Log.d("zarea4", "in client sertRecentChat: $error")
                }
            }
    }


    override fun sendMessage(message: Message): Flow<State<String>> = callbackFlow {
        database.reference.child(MESSAGES).child(message.groupId).push().setValue(
            message.toNetworkMessage()
        ) { error, ref ->
            if (error == null) {
                if (message.fileType != "") {
                    val key = ref.key
                    val storageRef = Firebase.storage
                        .getReference(currentUser!!.uid)
                        .child(key!!)
                        .child(message.fileURI.lastPathSegment!!)
                    putImageInStorage(storageRef, message, key)
                    trySend(State.SuccessWithData(ref.key!!))
                } else {
                    trySend(State.SuccessWithData(ref.key!!))
                }
            } else {

                trySend(State.Error(error.message))
            }
        }


        awaitClose()


    }


    private fun putImageInStorage(storageRef: StorageReference, message: Message, key: String) {
        storageRef.putFile(message.fileURI)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val message = message.copy(
                            fileURI = uri,
                            id = key
                        )

                        database.reference.child(MESSAGES).child(message.groupId).child(key)
                            .setValue(
                                message.toNetworkMessage()
                            ).addOnSuccessListener {
                                Log.d(TAG, "putImageInStorage: ")
                            }.addOnFailureListener {
                                Log.d(TAG, "putImageInStorage: ")
                            }

                    }

            }

    }

    override fun getMessages(chatId: String): Flow<State<List<Message>>> = callbackFlow {
        val ref = database.reference.child(MESSAGES).child(chatId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("dataChanged", "in fire client: $snapshot")
                val messages = mutableListOf<Message>()
                for (messageSnapshot in snapshot.children) {
                    val networkMessage = messageSnapshot.getValue(NetworkMessage::class.java)
                    if (networkMessage != null) {
                        val message = networkMessage.toModel(messageSnapshot.key!!, chatId).copy(
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
                        for (chatId in chatsId) {
                            val recentChats =
                                ((snapshot.child(chatId)).getValue(NetworkRecentChat::class.java)
                                    ?.toRecentChat(chatId))


                            if (recentChats != null) {
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

    override fun insertChatToUser(
        chatId: String,
        userEmail: String,
        receiverEmail: String
    ): Flow<State<String>> =
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
                    val newChats: Map<String, Boolean> = chats.associate { it ->
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
            Log.d("SEERDE", "call reached client: 1-$userEmail 2-$otherUserEmail")
            val ref = database.reference.child(PRIVATE_CHAT).child(userEmail).child(RECEIVER_USER)
                .child(otherUserEmail)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("mohamed21", "onDataChange: ${snapshot.key}55${snapshot.value}")
                    if (snapshot.exists()) {
                        Log.d("mohamed22", "onDataChange: ${snapshot.key}55${snapshot.value}")
                        Log.d("SEERDE", "onDataChange: user exists in client")
                        trySend(State.SuccessWithData(snapshot.value.toString()))
                    } else {
                        Log.d("SEERDE", "onDataChange: user doesn't exist in client")
                        trySend(State.SuccessWithData("-1"))
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: ${error.message}")
                    trySend(State.Error(error.message))
                }

            }
            ref.addListenerForSingleValueEvent(listener)
            Log.d("SEERDE", "haveChatWithUser: reached end of client function")
            awaitClose {
                ref.removeEventListener(listener)
            }
        }

    override fun updateRecentChat(recentChat: RecentChat, chatId: String): Flow<State<String>> =
        callbackFlow {
            val map = hashMapOf<String, Any>()
            map["lastMessage"] = recentChat.lastMessage
            map["timestamp"] = recentChat.timestamp
            map["id"] = chatId

            database.reference.child(RECENT_CHATS).child(chatId).updateChildren(
                map
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
        val userEmail = removeSpecialCharacters(email)
        Log.d("logF", email)
        removeUserFromGroup(chatId)
        val usersReference = database.reference.child("chats").child(chatId).child("members")
        usersReference.child(userEmail).removeValue().addOnSuccessListener {
            println("Left the group successfully!")
        }
            .addOnFailureListener {
                println("Failed to leave the group: ${it.message}")
            }
    }


    private fun removeUserFromGroup(groupId: String) {
        val email = Firebase.auth.currentUser?.email!!
        val userEmail = removeSpecialCharacters(email)
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
                val messages = snapshot.children.mapNotNull {
                    (it.getValue(NetworkMessage::class.java))?.toModel(
                        it.key!!,
                        groupId
                    )
                }
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

    override fun sendGroupMessage(message: Message, recentChat: RecentChat): Flow<State<Nothing>> =
        callbackFlow {
            Log.d("edrees", "Before Sending")
            trySend(State.Loading)
            database.reference.child(MESSAGES)
                .child(message.groupId).push().setValue(
                    message.toNetworkMessage()
                ) { error, ref ->
                    if (error == null) {
                        if (message.fileType != "") {
                            val key = ref.key
                            val storageRef = Firebase.storage
                                .getReference(currentUser!!.uid)
                                .child(key!!)
                                .child(message.fileURI.lastPathSegment!!)
                            putImageInStorage(storageRef, message, key)
                        }
                        val updates = HashMap<String, Any>()
                        updates["lastMessage"] = recentChat.lastMessage
                        updates["timestamp"] = recentChat.timestamp
                        database.reference.child(RECENT_CHATS)
                            .child(message.groupId)
                            .updateChildren(updates)
                            .addOnSuccessListener {
                                trySend(State.Success)
                            }.addOnFailureListener {
                                trySend(State.Error(it.localizedMessage!!))
                            }

                    } else {
                        trySend(State.Error(error.message))
                    }
                }

            awaitClose()
        }

    override fun createGroupChat(
        group: NetworkChatGroup,
        recentChat: NetworkRecentChat
    ): Flow<State<String>> = callbackFlow {
        Log.d("zarea3", "Before Sending ${recentChat.senderPicUrl}")
        try {
            val chatRef = database.reference.child(CHAT).push()
            val chatKey = chatRef.key
            if (chatKey == null) {
                trySend(State.Error("Failed to generate a chat key"))
                return@callbackFlow
            }
            if(recentChat.senderPicUrl.isBlank()){
                chatRef.setValue(group.copy(picURL = ""))
                    .addOnSuccessListener {
                        database.reference.child(RECENT_CHATS).child(chatKey)
                            .setValue(recentChat.toRecentChat(chatKey))
                            .addOnSuccessListener {
                                val userGroupData = hashMapOf<String, Any>()
                                for (user in group.members.entries) {
                                    userGroupData["$CHAT_USER/${
                                        removeSpecialCharacters(
                                            user.key
                                        )
                                    }/$GROUP/${chatKey}"] = user.value
                                }
                                val updateResult =
                                    database.reference.updateChildren(userGroupData)
                                        .addOnSuccessListener {
                                            trySend(State.SuccessWithData(chatKey))
                                        }.addOnFailureListener {
                                            trySend(State.Error("Failed to update user groups"))
                                        }
                            }
                            .addOnFailureListener {
                                trySend(
                                    State.Error(
                                        it.localizedMessage
                                            ?: "Failed to create recent chat"
                                    )
                                )
                            }
                    }
            } else {
                Firebase.storage
                    .getReference(currentUser!!.uid)
                    .child(chatKey)
                    .child(recentChat.senderPicUrl.toUri().lastPathSegment!!)
                    .putFile(recentChat.senderPicUrl.toUri())
                    .addOnSuccessListener {
                        it.metadata?.reference?.downloadUrl
                            ?.addOnSuccessListener { uri ->
                                Log.d("zarea3", "Image Uploaded ${uri}")
                                chatRef.setValue(group.copy(picURL = uri.toString()))
                                    .addOnSuccessListener {
                                        database.reference.child(RECENT_CHATS).child(chatKey)
                                            .setValue(recentChat.copy(senderPicUrl = uri.toString()).toRecentChat(chatKey))
                                            .addOnSuccessListener {
                                                val userGroupData = hashMapOf<String, Any>()
                                                for (user in group.members.entries) {
                                                    userGroupData["$CHAT_USER/${
                                                        removeSpecialCharacters(
                                                            user.key
                                                        )
                                                    }/$GROUP/${chatKey}"] = user.value
                                                }
                                                val updateResult =
                                                    database.reference.updateChildren(userGroupData)
                                                        .addOnSuccessListener {
                                                            trySend(State.SuccessWithData(chatKey))
                                                        }.addOnFailureListener {
                                                            trySend(State.Error("Failed to update user groups"))
                                                        }
                                            }
                                            .addOnFailureListener {
                                                trySend(
                                                    State.Error(
                                                        it.localizedMessage
                                                            ?: "Failed to create recent chat"
                                                    )
                                                )
                                            }
                                    }
                                    .addOnFailureListener {
                                        trySend(
                                            State.Error(
                                                it.localizedMessage ?: "Failed to create group chat"
                                            )
                                        )
                                    }
                            }
                    }
            }
        } catch (e: Exception) {
            trySend(State.Error(e.localizedMessage ?: "An error occurred"))
        }
        awaitClose()
    }

    override fun getGroupDetails(groupId: String): Flow<State<ChatGroup>> = callbackFlow {
        trySend(State.Loading)
        val groupReference = database.reference.child(CHAT).child(groupId)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = snapshot.getValue(NetworkChatGroup::class.java)?.toChatGroup(groupId)
                if (group != null) {
                    trySend(State.SuccessWithData(group))
                } else {
                    trySend(State.Error("Group Object is Null"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(State.Error(error.message))
            }
        }

        groupReference.addValueEventListener(valueEventListener)

        awaitClose { groupReference.removeEventListener(valueEventListener) }
    }

    override fun removeMemberFromGroup(groupId: String, memberEmail: String): Flow<State<String>> =
        callbackFlow {
            val groupMembersRef = database.getReference("$CHAT/$groupId/members")
            val userGroupsRef = database.getReference(
                "$CHAT_USER/${
                    removeSpecialCharacters(memberEmail)
                }/groups"
            )
            try {
                groupMembersRef.child(removeSpecialCharacters(memberEmail)).removeValue()
                userGroupsRef.child(groupId).removeValue()
                trySend(State.Success)
            } catch (e: Exception) {
                trySend(State.Error("Error removing user from the group: ${e.message}"))
            } finally {
                awaitClose()
            }
        }

    override fun updateGroupAvatar(uri: Uri, oldURL: String, groupID: String): Flow<State<String>> = callbackFlow {
        trySend(State.Loading)
        Firebase.storage
            .getReference(currentUser!!.uid)
            .child(groupID)
            .child(uri.lastPathSegment!!)
            .putFile(uri)
            .addOnSuccessListener {
                it.metadata?.reference?.downloadUrl?.addOnSuccessListener {uri ->
                    val path1 = "$RECENT_CHATS/$groupID/senderPicUrl"
                    val path2 = "$CHAT/$groupID/picURL"
                    val recentChatsRef: DatabaseReference = database.getReference(path1)
                    val chatsRef: DatabaseReference = database.getReference(path2)
                    val updateMap = mapOf(
                        path1 to uri.toString(),
                        path2 to uri.toString()
                    )
                    database.reference.updateChildren(updateMap).addOnSuccessListener {
                        if(oldURL.isBlank()){
                            trySend(State.SuccessWithData(uri.toString()))
                        } else {
                            val oldRef = Firebase.storage.getReferenceFromUrl(oldURL)
                            oldRef.delete().addOnSuccessListener {
                                trySend(State.SuccessWithData(uri.toString()))
                            }.addOnFailureListener{
                                trySend(State.Error("Failed to delete old image: ${it.message}"))
                            }
                        }
                    }.addOnFailureListener {
                        trySend(State.Error("Failed to update realtime references: ${it.message}"))
                    }
                }?.addOnFailureListener {
                    trySend(State.Error("Failed to retrieve download link: ${it.message}"))
                }
            }.addOnFailureListener {
                trySend(State.Error("Failed to upload the nem image: ${it.message}"))
            }

        awaitClose()
    }

    override fun addGroupMembers(groupId: String, usersEmails: List<String>): Flow<State<Nothing>> =
        callbackFlow {
            trySend(State.Loading)
//            val successCounter = AtomicInteger(0)
//
//            fun checkCompletion(counter: Int, totalUpdates: Int) {
//                if (counter == totalUpdates * 2) {
//                    trySend(State.Success)
//                    close()  // Close the flow when all updates are completed
//                }
//            }
//
//            usersEmails.forEach { userEmail ->
//                database.reference.child(CHAT_USER)
//                    .child(RemoveSpecialChar.removeSpecialCharacters(userEmail)).child(GROUP)
//                    .child(groupId)
//                    .setValue(false)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            successCounter.incrementAndGet()
//                            checkCompletion(successCounter.get(), usersEmails.size)
//                        } else {
//                            trySend(State.Error("$userEmail update failed"))
//                        }
//                    }
//            }
            Log.d("SEERDE", "addGroupMembers: Call reached client")
            val groupUpdate = mutableMapOf<String, Any>()
            usersEmails.forEach { userEmail ->
                val userKey = removeSpecialCharacters(userEmail)
                groupUpdate["$CHAT/$groupId/members/$userKey"] = false
                groupUpdate["$CHAT_USER/$userKey/$GROUP/$groupId"] = false
            }
            Log.d("SEERDE", "addGroupMembers: updates: $groupUpdate")
            database.reference.updateChildren(groupUpdate)
                .addOnSuccessListener {
                    Log.d("SEERDE", "addGroupMembers: Success in client")
                    trySend(State.Success)
                }.addOnFailureListener {
                    trySend(State.Error("Chat group update failed"))
                }
            awaitClose {}
        }

    override fun changeGroupName(groupID: String, newName: String):Flow<State<Nothing>> = callbackFlow{
        trySend(State.Loading)
        Log.d("SEERDE", "changeGroupName: Before calling")
        database.reference.child(RECENT_CHATS)
            .child(groupID)
            .child("title")
            .setValue(newName)
            .addOnSuccessListener {
                database.reference.child(CHAT).child(groupID).child("name").setValue(newName).addOnSuccessListener {
                    trySend(State.Success)
                }.addOnFailureListener {
                    trySend(State.Error(it.message!!))
                }
            }.addOnFailureListener {
                trySend(State.Error(it.message!!))
            }
        awaitClose { /* cleanup resources if needed */ }
    }

}