package com.gp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gp.core.database.dao.ReplyDao
import com.gp.core.database.model.Post
import com.gp.core.database.model.Reply

@Database(
    entities=[Post::class, Reply::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun postDao(): ReplyDao


}