package com.gp.socialapp.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gp.socialapp.database.model.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(vararg post: PostEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePost(post: PostEntity)

    @Query("select * from posts")
    fun getAllPosts(): Flow<List<PostEntity>>
    @Query("select * from posts where id = :id")
    fun getPostById(id: String): Flow<PostEntity>

    @Delete
    suspend fun deletePost(post: PostEntity)

    @Query("SELECT * FROM posts WHERE title LIKE '%' || :searchText || '%' ")
    fun searchPostsByTitle(searchText: String): Flow<List<PostEntity>>
    @Query("DELETE FROM posts")
    fun deleteAllPosts()

}