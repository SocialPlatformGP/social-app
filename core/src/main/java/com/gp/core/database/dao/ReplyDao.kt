package com.gp.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gp.core.database.model.Reply
import com.gp.core.database.model.relationship.PostWithReplies
import kotlinx.coroutines.flow.Flow

@Dao
interface ReplyDao {

    @Insert
    suspend fun insertReply(reply: Reply): Long

    @Insert
    suspend fun insertReplies(replies: List<Reply>)

    @Update
    suspend fun updateReply(reply: Reply)

    @Update
    suspend fun updateReplies(replies: List<Reply>)


    @Delete
    suspend fun deleteReply(reply: Reply)

    @Delete
    suspend fun deleteReplies(replies: List<Reply>)


    @Query("DELETE FROM replies")
    suspend fun deleteAllReplies()

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