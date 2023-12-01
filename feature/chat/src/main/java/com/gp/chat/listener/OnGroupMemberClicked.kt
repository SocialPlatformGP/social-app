package com.gp.chat.listener

import com.gp.users.model.User

interface OnGroupMemberClicked {
    fun onMemberClicked(user: User)
}