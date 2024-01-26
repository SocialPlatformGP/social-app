package com.gp.socialapp.utils

sealed class State<out T> {
     object Success : State<Nothing>()
    data class SuccessWithData<T>(var data: T) : State<T>()
    data class Error<T>(var message: String) : State<T>()
     object Loading : State<Nothing>()
     object Idle : State<Nothing>()
}
