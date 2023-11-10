package com.gp.chat.listener

interface OnRecentChatClicked {
    fun onRecentChatClicked(
        chatId: String,
        receiverName: String,
        senderName: String,
        receiverImage: String=""
    )
}