package com.gp.posts.listeners

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.Post

interface PostOnClickListener {
    fun onPostClicked(post: Post)

}