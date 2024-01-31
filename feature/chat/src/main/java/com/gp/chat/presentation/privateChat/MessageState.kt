package com.gp.chat.presentation.privateChat

import android.net.Uri
import androidx.core.net.toUri

data class MessageState(
    var message: String = "",
    var error: String = "",
    var fileUri: Uri = "".toUri(),
    var fileType: String = "",
    var fileName: String = "",
)