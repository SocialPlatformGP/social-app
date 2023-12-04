package com.gp.chat.listener

interface OnRecentChatClicked {
    fun onRecentChatClicked(
        chatId: String,
        receiverName: String,
        senderName: String,
        receiverImage: String="",
        isPrivateChat: Boolean,
        senderPicUrl:String="",
        receiverPicUrl:String=""
    )

    fun leaveGroup(groupId:String)


}