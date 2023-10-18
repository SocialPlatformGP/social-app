package com.gp.socialapp.utils

sealed class State<out T> {
    data class SuccessWithData<T>(var data: T) : State<T>()
    data object Success : State<Nothing>()

    data class Error<T>(var message: String) : State<T>()

    data object Loading : State<Nothing>()
    data object Idle : State<Nothing>()



}
