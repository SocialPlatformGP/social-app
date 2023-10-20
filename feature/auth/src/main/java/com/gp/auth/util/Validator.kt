package com.gp.auth.util

object Validator {
    object PasswordValidator {
        fun validateLength(password: String) = password.length >= 8
        fun validateChars(password: String) =
            password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).*\$".toRegex())

        fun validateAll(password: String) = validateLength(password) && validateChars(password)
    }

    object EmailValidator {
        fun validateAll(email: String) =
            email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex())
    }
}