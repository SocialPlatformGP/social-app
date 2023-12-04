package com.gp.material.model

import android.net.Uri

data class MaterialItem(
    val path:String="",
    val type:String="",
    val name:String="",
    val createdBy:String="",
    val data: Uri,
    val creationTime:String=""
    )
