package com.gp.socialapp.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.database.model.relationship.PostWithReplies
import kotlinx.coroutines.flow.Flow

@Dao
interface ReplyDao {

    @Insert
    suspend fun insertReply(replyEntity: ReplyEntity): Long

    @Insert
    suspend fun insertReplies(replies: List<ReplyEntity>)

    @Update
    suspend fun updateReply(replyEntity: ReplyEntity)

    @Update
    suspend fun updateReplies(replies: List<ReplyEntity>)


    @Delete
    suspend fun deleteReply(replyEntity: ReplyEntity)

    @Delete
    suspend fun deleteReplies(replies: List<ReplyEntity>)


    @Query("DELETE FROM replies")
    suspend fun deleteAllReplies()

    @Query("SELECT * FROM replies")
    fun getAllReplies(): Flow<List<ReplyEntity>>

    @Query("SELECT * FROM replies WHERE postId = :postId")
    fun getRepliesByPostId(postId: String): Flow<List<ReplyEntity>>

    @Query("SELECT * FROM replies WHERE id = :id")
    fun getReplyById(id: Long): Flow<ReplyEntity>

    @Query("SELECT * FROM replies WHERE parentReplyId = :parentReplyId")
    fun getRepliesByParentReplyId(parentReplyId: Long): Flow<List<ReplyEntity>>

    @Query("SELECT * FROM replies WHERE parentReplyId IS NULL AND postId = :postId")
    fun getTopLevelRepliesByPostId(postId: String): Flow<List<ReplyEntity>>





}