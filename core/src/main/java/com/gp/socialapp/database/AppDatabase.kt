package com.gp.socialapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.gp.socialapp.database.dao.PostDao
import com.gp.socialapp.database.dao.ReplyDao
import com.gp.socialapp.database.dao.UserDao
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.database.model.UserEntity

@Database(
    entities=[PostEntity::class, ReplyEntity::class,UserEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun replyDao(): ReplyDao
    abstract fun postDao(): PostDao
    abstract fun userDao():UserDao

}