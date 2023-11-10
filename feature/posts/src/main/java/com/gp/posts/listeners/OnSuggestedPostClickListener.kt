package com.gp.posts.listeners

import com.gp.socialapp.model.Post

interface OnSuggestedPostClickListener {
    fun onClick( model: Post)
}