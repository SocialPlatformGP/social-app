package com.gp.chat.listener

import com.gp.users.model.User

interface OnGroupMembersChangeListener {
    fun onAddGroupMember(user: User)
    fun onRemoveGroupMember(user: User)
}