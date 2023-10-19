package com.gp.auth.ui.registration

import java.util.Date

data class UserInformationUIState(
    var firstName: String,
    var lastName: String,
    var phoneNumber: String,
    var birthDate: Date,
    var bio: String
){
    constructor(): this("", "", "", Date(), "")
}
