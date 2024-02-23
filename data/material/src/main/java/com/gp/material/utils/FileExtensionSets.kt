package com.gp.material.utils

import com.gp.material.model.FileType

class FileExtensionSets {
    companion object {
        private val imageExtensions = setOf("jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp")
        private val pdfExtensions = setOf("pdf")
        private val audioExtensions = setOf("mp3", "wav", "ogg", "flac", "aac")
        private val excelExtensions = setOf("xls", "xlsx", "csv")
        private val pptExtensions = setOf("ppt", "pptx")
        private val textExtensions = setOf("txt", "log", "md")
        private val wordExtensions = setOf("doc", "docx", "rtf")
        private val zipExtensions = setOf("zip", "rar", "7z")
        private val videoExtensions = setOf("mp4", "avi", "mkv", "wmv", "flv", "mov")

        fun getFileType(extension: String): FileType {
            return when (extension.lowercase()) {
                in imageExtensions -> FileType.IMAGE
                in pdfExtensions -> FileType.PDF
                in audioExtensions -> FileType.AUDIO
                in excelExtensions -> FileType.EXCEL
                in pptExtensions -> FileType.PPT
                in textExtensions -> FileType.TEXT
                in wordExtensions -> FileType.WORD
                in zipExtensions -> FileType.ZIP
                in videoExtensions -> FileType.VIDEO
                else -> FileType.OTHER
            }
        }
    }
}