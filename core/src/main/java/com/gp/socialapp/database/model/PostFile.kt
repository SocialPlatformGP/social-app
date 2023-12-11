package com.gp.socialapp.database.model

import android.net.Uri


data class PostAttachment(
    val url: String = "",
    val name: String = "",
    val type: String = "",
    val size: Long = 0
)
enum class MimeType(val value: String, val readableType: String) {
    IMAGE("image/*", "Image"),
    JPEG("image/jpeg", "JPEG Image"),
    PNG("image/png", "PNG Image"),
    GIF("image/gif", "GIF Image"),
    BMP("image/bmp", "BMP Image"),
    TIFF("image/tiff", "TIFF Image"),
    WEBP("image/webp", "WEBP Image"),

    VIDEO("video/*", "Video"),
    MP4("video/mp4", "MP4 Video"),
    AVI("video/avi", "AVI Video"),
    MKV("video/x-matroska", "MKV Video"),
    MOV("video/quicktime", "QuickTime Video"),
    WMV("video/x-ms-wmv", "WMV Video"),

    AUDIO("audio/*", "Audio"),
    MP3("audio/mpeg", "MP3 Audio"),
    WAV("audio/wav", "WAV Audio"),
    AAC("audio/aac", "AAC Audio"),
    OGG("audio/ogg", "OGG Audio"),
    FLAC("audio/flac", "FLAC Audio"),

    TEXT("text/plain", "Text"),
    HTML("text/html", "HTML Document"),
    CSS("text/css", "CSS Document"),
    JAVASCRIPT("application/javascript", "JavaScript"),

    PDF("application/pdf", "PDF"),

    WORD("application/msword", "Word Document"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Word Document (DOCX)"),

    EXCEL("application/vnd.ms-excel", "Excel Document"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Excel Document (XLSX)"),

    POWERPOINT("application/vnd.ms-powerpoint", "PowerPoint Presentation"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", "PowerPoint Presentation (PPTX)"),

    ZIP("application/zip", "ZIP Archive"),
    TAR("application/x-tar", "TAR Archive"),
    GZIP("application/gzip", "GZIP Archive"),
    RAR("application/x-rar-compressed", "RAR Archive"),

    CSV("text/csv", "CSV Document"),
    XML("text/xml", "XML Document"),
    JSON("application/json", "JSON Document"),

    SVG("image/svg+xml", "SVG Image"),

    MARKDOWN("text/markdown", "Markdown Document"),

    JAVA("text/x-java-source", "Java Source Code"),

    SQLITE("application/x-sqlite3", "SQLite Database"),

    APK("application/vnd.android.package-archive", "Android Package"),

    ALL_FILES("*/*", "All Files")
}
data class PostFile(
    val uri: Uri,
    val name: String,
    val type: MimeType)