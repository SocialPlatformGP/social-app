package com.gp.socialapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gp.socialapp.models.Post
import com.gp.socialapp.models.Reply
import com.gp.socialapp.models.relationship.PostWithReplies
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Insert
     suspend fun insertPost(post: Post): Long

    @Insert
    suspend fun insertPosts(posts: List<Post>)

    @Insert
    suspend fun insertReply(reply: Reply): Long

    @Insert
    suspend fun insertReplies(replies: List<Reply>)

    @Update
    suspend fun updatePost(post: Post)

    @Update
    suspend fun updatePosts(posts: List<Post>)

    @Update
    suspend fun updateReply(reply: Reply)

    @Update
    suspend fun updateReplies(replies: List<Reply>)


    @Delete
    suspend fun deletePost(post: Post)

    @Delete
    suspend fun deletePosts(posts: List<Post>)

    @Delete
    suspend fun deleteReply(reply: Reply)

    @Delete
    suspend fun deleteReplies(replies: List<Reply>)


    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    @Query("DELETE FROM replies")
    suspend fun deleteAllReplies()

    @Query("SELECT * FROM posts")
    fun getAllPosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :id")
    fun getPostById(id: Long): Flow<Post>

    @Query("SELECT * FROM replies")
    fun getAllReplies(): Flow<List<Reply>>

    @Query("SELECT * FROM replies WHERE postId = :postId")
    fun getRepliesByPostId(postId: Long): Flow<List<Reply>>

    @Query("SELECT * FROM replies WHERE id = :id")
    fun getReplyById(id: Long): Flow<Reply>

    @Query("SELECT * FROM replies WHERE parentReplyId = :parentReplyId")
    fun getRepliesByParentReplyId(parentReplyId: Long): Flow<List<Reply>>

    @Query("SELECT * FROM replies WHERE parentReplyId IS NULL AND postId = :postId")
    fun getTopLevelRepliesByPostId(postId: Long): Flow<List<Reply>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostwithReplies(postId: Long): Flow<PostWithReplies>

    @Query("SELECT * FROM posts")
    fun getAllPostswithReplies(): Flow<List<PostWithReplies>>


}