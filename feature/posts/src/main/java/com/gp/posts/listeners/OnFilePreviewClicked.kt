package com.gp.posts.listeners

import com.gp.socialapp.database.model.PostFile

interface OnFilePreviewClicked {
    fun onFilePreviewClicked(file: PostFile)
    fun onFileRemoveClicked(file: PostFile)
}