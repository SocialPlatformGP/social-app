package com.gp.socialapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gp.socialapp.database.dao.ReplyDao
import com.gp.socialapp.database.model.Post
import com.gp.socialapp.database.model.Reply

@Database(
    entities=[Post::class, Reply::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun postDao(): ReplyDao


}