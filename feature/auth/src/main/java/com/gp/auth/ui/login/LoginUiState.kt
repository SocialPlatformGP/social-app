package com.gp.auth.ui.login

import com.google.firebase.auth.FirebaseUser
import com.gp.socialapp.utils.State

data class LoginUiState(
    var email: String,
    var password: String,
    var isSignedIn: State<FirebaseUser>
){
    constructor(): this("", "", State.Idle)
}
