package com.gp.chat.listener

import com.gp.chat.model.RecentChat

interface OnRecentChatClicked {
    fun onRecentChatClicked(
        recentChat: RecentChat
    )

    fun leaveGroup(groupId:String)


}