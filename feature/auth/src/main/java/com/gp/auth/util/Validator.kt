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
    object PhoneNumberValidator{
        fun validateAll(phoneNumber: String) =
            phoneNumber.matches("^01[0-2]{1}[0-9]{8}\$".toRegex())
    }
    object BirthDateValidator{
        //min age is 17
        fun validateAll(birthDate: String) =
            birthDate.matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/((19|20)\\d\\d)\$".toRegex())

    }
}