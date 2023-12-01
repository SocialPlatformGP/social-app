package com.gp.posts.listeners

import com.gp.socialapp.model.Post

interface OnPostClickListener {
    fun onPostClicked(post: Post)

}