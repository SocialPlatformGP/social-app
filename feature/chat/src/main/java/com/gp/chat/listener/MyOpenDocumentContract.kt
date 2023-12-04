package com.gp.chat.listener


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

class MyOpenActionContract : ActivityResultContract<String, Uri?>() {

    override fun createIntent(context: Context, input: String): Intent {
        return when (input) {
            "camera" -> {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)


            }
            "gallery" -> {
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            }
            "file" -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*" // Set the MIME type here to specify the type of file to select, or use */* for all types
                intent
            }
            else -> throw IllegalArgumentException("Invalid action")
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data
    }
}
