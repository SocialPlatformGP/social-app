package com.gp.material.listener

import com.gp.material.model.MaterialItem

interface ClickOnFileClicKListener {
    fun deleteFile(item: MaterialItem)
    fun openFile(item: MaterialItem)
    fun downloadFile(item: MaterialItem)
    fun openFolder(path:String)
    fun deleteFolder(folderPath:String)
    fun shareLink(item: MaterialItem)
}