package com.gp.auth.ui.registration

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.gp.auth.R
import com.gp.socialapp.utils.State
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Date

data class UserInformationUIState(
    var firstName: String,
    var lastName: String,
    var phoneNumber: String,
    var birthDate: Date,
    var bio: String,
    var pfp: Drawable?,
    val createdState: State<Nothing>
) : BaseObservable() {
    constructor(): this("", "", "", Date(), "", null, State.Idle)
}
