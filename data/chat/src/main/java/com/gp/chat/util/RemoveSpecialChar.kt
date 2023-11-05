package com.gp.chat.util

object RemoveSpecialChar {
    fun removeSpecialCharacters(email: String): String {
        return email.replace(Regex("[@.#]"), "")
    }
}