package com.gp.socialapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gp.socialapp.database.dao.PostDao
import com.gp.socialapp.database.dao.ReplyDao
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.Reply

@Database(
    entities=[PostEntity::class, Reply::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun replyDao(): ReplyDao
    abstract fun postDao(): PostDao

}