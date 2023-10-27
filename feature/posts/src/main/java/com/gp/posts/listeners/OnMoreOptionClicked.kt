package com.gp.posts.listeners

import com.google.android.material.button.MaterialButton
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Reply

interface OnMoreOptionClicked {
    fun onMoreOptionClicked(imageView5: MaterialButton, postitem: Post)
    fun onMoreOptionClicked(imageView5: MaterialButton, reply: Reply)
}