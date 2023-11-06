package com.gp.chat.util

object RemoveSpecialChar {
    fun removeSpecialCharacters(email: String): String {
        return email.replace(Regex("[@.#]"), " ")
    }
    fun restoreOriginalEmail(email: String): String {
        val modifiedEmail = email.replaceFirst(" ", "@")
        val originalEmail = modifiedEmail.replaceFirst(" ", ".")
        return originalEmail
    }
}