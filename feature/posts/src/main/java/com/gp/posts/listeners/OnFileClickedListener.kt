package com.gp.posts.listeners

import com.gp.socialapp.database.model.PostAttachment

interface OnFileClickedListener {
    fun onFileClicked(attachment: PostAttachment)
}