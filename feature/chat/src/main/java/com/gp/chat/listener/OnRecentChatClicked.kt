package com.gp.chat.listener

import com.gp.chat.model.RecentChat

interface OnRecentChatClicked {
    fun onClick(
        recentChat: RecentChat
    )
    fun onLongClick(recentChat: RecentChat)
}