package com.gp.material.utils


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class MyOpenActionContract : ActivityResultContract<String, List<Uri>>() {
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        // The resultCode can be RESULT_OK only or RESULT_CANCELED.
        if (resultCode != android.app.Activity.RESULT_OK) {
            return emptyList()
        }
        return if (intent?.clipData != null) {
            val count = intent.clipData!!.itemCount
            val uris = mutableListOf<Uri>()
            for (i in 0 until count) {
                uris.add(intent.clipData!!.getItemAt(i).uri)
            }
            uris
        } else {
            listOfNotNull(intent?.data)
        }

    }


}
