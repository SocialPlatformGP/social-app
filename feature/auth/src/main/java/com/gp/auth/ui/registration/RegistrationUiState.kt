package com.gp.auth.ui.registration

import com.google.firebase.auth.FirebaseUser
import com.gp.socialapp.utils.State

data class RegistrationUiState(
    var email: String,
    var password: String,
    var rePassword: String,
    var isSignedUp: State<FirebaseUser>
) {
    constructor(): this("", "", "", State.Idle)
}
