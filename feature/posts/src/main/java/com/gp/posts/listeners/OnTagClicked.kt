package com.gp.posts.listeners

import com.gp.socialapp.model.Tag


interface OnTagClicked {
    fun onTagClicked(tag: Tag)
}