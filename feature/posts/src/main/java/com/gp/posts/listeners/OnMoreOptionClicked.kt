package com.gp.posts.listeners

import com.google.android.material.button.MaterialButton
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity

interface OnMoreOptionClicked {
    fun onMoreOptionClicked(imageView5: MaterialButton, postitem: PostEntity)
    fun onMoreOptionClicked(imageView5: MaterialButton, reply: ReplyEntity)
}