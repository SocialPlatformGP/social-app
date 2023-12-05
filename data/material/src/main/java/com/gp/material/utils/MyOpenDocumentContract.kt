package com.gp.material.utils


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class MyOpenActionContract : ActivityResultContracts.GetMultipleContents() {

    override fun createIntent(context: Context, input: String): Intent {
        return super.createIntent(context, input)
    }

}
