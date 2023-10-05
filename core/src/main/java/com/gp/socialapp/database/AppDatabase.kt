package com.gp.socialapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gp.socialapp.dao.PostDao
import com.gp.socialapp.models.Post
import com.gp.socialapp.models.Reply

@Database(
    entities=[Post::class, Reply::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun postDao(): PostDao


}