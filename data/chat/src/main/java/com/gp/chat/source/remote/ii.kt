//package com.gp.chat.source.remote
//
//chatId = databaseReference.child("messages").push().key!!
//databaseReference.child("privateChats").child(userEmail).child("receivers").child(receiverEmail).setValue(chatId)
//databaseReference.child("privateChats").child(receiverEmail).child("receivers").child(userEmail).setValue(chatId)
//databaseReference.child("recentChats").child(userEmail).child(chatId).setValue(NetworkRecentChat(
//timestamp = Date().toString(),
//senderName = userEmail,
//receiverName = receiverEmail,
//))
//databaseReference.child("recentChats").child(receiverEmail).child(chatId).setValue(NetworkRecentChat(
//timestamp = Date().toString(),
//senderName = userEmail,
//receiverName = receiverEmail,
//))
//
//Log.d("MFC chatId my  new  chat id  ", "checkIfNewChat: $chatId")
//
//
//
//
//
//
//Log.d("MFC snapshot is not exist", "checkIfNewChat: $snapshot")
//reference.child(userEmail).child("receivers").child(receiverEmail).setValue("")
//reference.child(receiverEmail).child("receivers").child(userEmail).setValue("")
//val chatId = databaseReference.child("messages").push().key!!
//databaseReference.child("privateChats").child(userEmail).child("receivers").child(receiverEmail).setValue(chatId)
//databaseReference.child("privateChats").child(receiverEmail).child("receivers").child(userEmail).setValue(chatId)
//databaseReference.child("recentChats").child(userEmail).child(chatId).setValue(NetworkRecentChat(
//timestamp = Date().toString(),
//senderName = userEmail,
//receiverName = receiverEmail,
//))
//databaseReference.child("recentChats").child(receiverEmail).child(chatId).setValue(NetworkRecentChat(
//timestamp = Date().toString(),
//senderName = userEmail,
//receiverName = receiverEmail,
//))
//Log.d("MFC chatId my  new  chat id and new private chat  ", "checkIfNewChat: $chatId")
//trySend(State.SuccessWithData(chatId))
