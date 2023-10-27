package com.gp.auth.ui.passwordreset

import com.gp.socialapp.utils.State

data class PasswordResetUiState (
    var email: String = "",
    var sentState: State<Nothing> = State.Idle
        )