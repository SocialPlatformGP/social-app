package com.gp.socialapp.database

import com.gp.socialapp.database.model.TagEntity
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
}