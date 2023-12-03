package com.gp.chat.presentation.privateChat

import android.net.Uri
import androidx.core.net.toUri

data class MessageState(
    var message: String = "",
    var error: String = "",
    var fileUri: Uri = "https://www.google.com/images/spin-32.gif".toUri(),
    var fileTypes: String = "text",
    var fileName: String = "",

)