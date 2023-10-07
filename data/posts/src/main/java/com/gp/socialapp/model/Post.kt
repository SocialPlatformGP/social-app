package com.gp.socialapp.model

data class Post(val todo: String){
    init{
        TODO("attributes: Title, Author name, publish date, body, upvotes, downvotes, editStatus")
    }
}
