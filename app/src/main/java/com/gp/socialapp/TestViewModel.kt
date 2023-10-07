package com.gp.socialapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.posts.repository.PostRepository
import com.gp.socialapp.models.Post
import com.gp.socialapp.models.Reply
import com.gp.socialapp.models.relationship.PostWithReplies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Flow

import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val repository: PostRepository
) :ViewModel() {

    data class NestedReplyItem(
        val postId: Long,
        val parentReply: Long?,
        val reply: Reply,
        val replies: List<NestedReplyItem>
    )

    data class PostItem(
        val post: Post,
        val replies: List<NestedReplyItem>
    )

    private fun buildNestedReplies(postWithReplies: List<PostWithReplies>): List<PostItem> {
        return postWithReplies.map { postWithReply ->
            val post = postWithReply.post
            val replies = postWithReply.replies
            val nestedRepliesList = buildNestedReplies(replies, null)
            PostItem(post = post, replies = nestedRepliesList)
        }
    }

    private fun buildNestedReplies(
        replies: List<Reply>,
        parentReplyId: Long?
    ): List<NestedReplyItem> {
        return replies
            .filter { it.parentReplyId == parentReplyId }
            .map { reply ->
                val nestedReplies = buildNestedReplies(replies, reply.id)
                NestedReplyItem(
                    postId = reply.postId,
                    parentReply = reply.parentReplyId,
                    reply = reply,
                    replies = nestedReplies
                )
            }
    }





    fun getPostwithReplies() {

        viewModelScope.launch {
            repository.getAllPostswithReplies().collect {
                val nestedReplies = buildNestedReplies(it)
                Log.d("TestViewModel", "getPostwithReplies: $nestedReplies")
                Log.d("TestViewModel", "getPostwithReplies25: $it")
            }
        }
    }

    fun insertdummy(){
        val post1 = Post( title = "Post 1", content = "Content of Post 1", upvotes = 10, downvotes = 2)
        val post2 = Post( title = "Post 2", content = "Content of Post 2", upvotes = 15, downvotes = 3)
        val post3 = Post( title = "Post 3", content = "Content of Post 3", upvotes = 15, downvotes = 3)
        val post4 = Post( title = "Post 4", content = "Content of Post 4", upvotes = 15, downvotes = 3)
        val post5 = Post( title = "Post 5", content = "Content of Post 5", upvotes = 15, downvotes = 3)
        val post6 = Post( title = "Post 6", content = "Content of Post 6", upvotes = 15, downvotes = 3)

        viewModelScope.launch (Dispatchers.IO){
            val postId1 = repository.insertPost(post1)
            val postId2 = repository.insertPost(post2)
            val postId3 = repository.insertPost(post3)
            val postId4 = repository.insertPost(post4)
            val postId5 = repository.insertPost(post5)
            val postId6 = repository.insertPost(post6)
            Log.d("TestViewModel2", "insertdummy: $postId1")

            val reply1 = Reply( postId = postId1, parentReplyId =  null, content =  "Reply 1 for Post 1", upvotes =  5, downvotes =  1, depth = 0)
            val id1=repository.insertReply(reply1)
            val reply2 = Reply( postId = postId1, parentReplyId =  null, content =  "Reply 2 for Post 1", upvotes =  8, downvotes =  2, depth = 0)
            val id2=repository.insertReply(reply2)
            val reply3 = Reply( postId = postId1, parentReplyId =  id1, content =  "Reply to Reply 1 for Post 1", upvotes =  1, downvotes =  1, depth = 1)
            val id3=repository.insertReply(reply3)
            val reply4 = Reply( postId = postId2, parentReplyId =  null, content =  "Reply 1 for Post 2", upvotes =  7, downvotes =  2, depth = 0)
            val id4=repository.insertReply(reply4)
            val reply5 = Reply( postId = postId2, parentReplyId =  null, content =  "Reply 2 for Post 2", upvotes =  9, downvotes =  2, depth = 0)
            val id5=repository.insertReply(reply5)
            //crate nested replies for each post
            val reply6 = Reply( postId = postId2, parentReplyId =  id4, content =  "Reply to Reply 1 for Post 2", upvotes =  1, downvotes =  1, depth = 1)
            val id6=repository.insertReply(reply6)
            val reply7 = Reply( postId = postId2, parentReplyId =  id6, content =  "Reply to Reply 2 for Post 2", upvotes =  1, downvotes =  1, depth = 2)
            val id7=repository.insertReply(reply7)
            val reply8 = Reply( postId = postId2, parentReplyId =  id5, content =  "Reply to Reply 1 for Post 2", upvotes =  1, downvotes =  1, depth = 1)
            val id8=repository.insertReply(reply8)
            val reply9 = Reply( postId = postId2, parentReplyId =  id8, content =  "Reply to Reply 2 for Post 2", upvotes =  1, downvotes =  1, depth = 2)
            val id9=repository.insertReply(reply9)
            val reply10 = Reply( postId = postId2, parentReplyId =  id9, content =  "Reply to Reply 3 for Post 2", upvotes =  1, downvotes =  1, depth = 3)
            val id10=repository.insertReply(reply10)
            val reply11 = Reply( postId = postId2, parentReplyId =  id10, content =  "Reply to Reply 4 for Post 2", upvotes =  1, downvotes =  1, depth = 4)
            val reply12 = Reply( postId = postId2, parentReplyId =  id5, content =  "Reply to Reply 5 for Post 2", upvotes =  1, downvotes =  1, depth = 1)



            repository.insertReply(reply2)
            repository.insertReply(reply3)
            repository.insertReply(reply4)
            repository.insertReply(reply5)
            repository.insertReply(reply6)
            repository.insertReply(reply7)
            repository.insertReply(reply8)
            repository.insertReply(reply9)
            repository.insertReply(reply10)
            repository.insertReply(reply11)
            repository.insertReply(reply12)

        }



    }


}