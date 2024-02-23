package com.gp.auth.ui.registration

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.gp.auth.R
import com.gp.socialapp.utils.State
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.util.Date

data class UserInformationUIState  (
    var firstName: String = "",
    var lastName: String = "",
    var phoneNumber: String = "",
    var birthDate:  Date = Date(),
    var bio: String ="",
    var pfpLocalURI: Uri = Uri.EMPTY,
    val createdState: State<Nothing> = State.Idle
) : BaseObservable()
