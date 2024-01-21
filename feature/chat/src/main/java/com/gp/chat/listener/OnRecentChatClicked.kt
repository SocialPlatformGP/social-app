package com.gp.chat.listener

import com.gp.chat.model.RecentChat
import com.gp.chat.presentation.home.DropDownItem

interface OnRecentChatClicked {
    fun onClick(
        recentChat: RecentChat
    )
    fun onDropDownItemClicked(dropDownItem: DropDownItem, recentChat: RecentChat)
}