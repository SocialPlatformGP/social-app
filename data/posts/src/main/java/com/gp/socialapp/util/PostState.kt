package com.gp.socialapp.util

sealed class PostState{
    data class Success(var boolean: Boolean): PostState()
    data class Error(var message:String): PostState()
    data object Loading : PostState()
    data object Idle : PostState()
}
