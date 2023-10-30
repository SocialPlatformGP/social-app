package com.gp.posts.listeners

import com.gp.socialapp.database.model.Tag

interface OnTagClicked {
    fun onTagClicked(tag: Tag)
}