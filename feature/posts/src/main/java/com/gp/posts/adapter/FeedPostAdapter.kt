package com.gp.posts.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.gp.posts.R
import com.gp.posts.databinding.ItemPostBinding
import com.gp.posts.listeners.OnMoreOptionClicked
import com.gp.posts.listeners.VotesClickedListener
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.Post

class FeedPostAdapter(
    val onMoreOptionClicked: OnMoreOptionClicked,
    val onPostClicked: VotesClickedListener,
    val context: Context
) : ListAdapter<Post, FeedPostAdapter.PostViewHolder>(PostDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        var binding: ItemPostBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_post,
            parent,
            false
        )
        binding.cardViewPost.setOnClickListener {
            onPostClicked.onPostClicked(binding.postitem!!)
        }
        binding.imageViewUpvotePost.setOnClickListener {
            onPostClicked.onUpVoteClicked(binding.postitem!!)
        }
        binding.imageViewDownvotePost.setOnClickListener {
            onPostClicked.onDownVoteClicked(binding.postitem!!)
        }
        binding.moreOptionPost.setOnClickListener {
            onMoreOptionClicked.onMoreOptionClicked(binding.moreOptionPost, binding.postitem!!)
        }
        binding.imgAddComment.setOnClickListener {
            onPostClicked.onPostClicked(binding.postitem!!)
        }

        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }


    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.postitem = post
            binding.context = context
            binding.executePendingBindings()
        }
    }

    private class PostDiffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

    }

}





