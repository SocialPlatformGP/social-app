package com.gp.socialapp.database

import android.net.Uri
import com.gp.socialapp.database.model.TagEntity
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gp.socialapp.database.model.MimeType
import com.gp.socialapp.database.model.PostAttachment
import java.util.Date

class Converter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTagList(tagList: List<TagEntity>): String {
        return Gson().toJson(tagList)
    }

    @TypeConverter
    fun toTagList(tagListString: String): List<TagEntity> {
        return Gson().fromJson(tagListString, object : TypeToken<List<TagEntity>>() {}.type)
    }
    @TypeConverter
    fun fromMimeType(mimeType: MimeType?): String? {
        return Gson().toJson(mimeType)
    }

    @TypeConverter
    fun toMimeType(value: String?): MimeType? {
        return Gson().fromJson(value, MimeType::class.java)
    }
    @TypeConverter
    fun fromPostFileList(fileList: List<PostAttachment>): String {
        return Gson().toJson(fileList)
    }

    @TypeConverter
    fun toPostFileList(fileListString: String): List<PostAttachment> {
        return Gson().fromJson(fileListString, object : TypeToken<List<PostAttachment>>() {}.type)
    }
}