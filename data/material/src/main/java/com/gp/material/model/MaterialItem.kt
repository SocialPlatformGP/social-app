package com.gp.material.model

data class MaterialItem(
    val id:String="",
    val path:String="",
    val fileType: FileType,
    val name:String="",
    val createdBy:String="",
    val fileUrl: String="",
    val creationTime:String=""
    )
enum class FileType {
    IMAGE,
    PDF,
    AUDIO,
    VIDEO,
    OTHER
}

