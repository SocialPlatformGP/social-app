package com.gp.posts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gp.posts.R
import com.gp.posts.databinding.ItemPostBinding
import com.gp.posts.listeners.OnMoreOptionClicked
import com.gp.posts.listeners.VotesClickedListener
import com.gp.socialapp.database.model.PostEntity


class FeedPostAdapter(
    val onMoreOptionClicked: OnMoreOptionClicked,
    val onPostClicked:VotesClickedListener
) : ListAdapter<PostEntity,FeedPostAdapter.PostViewHolder>(PostDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        var binding : ItemPostBinding = DataBindingUtil.inflate(
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
        binding.imageView5.setOnClickListener {
            onMoreOptionClicked.onMoreOptionClicked(binding.imageView5)
        }
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }



    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: PostEntity) {
            binding.postitem = post
            binding.executePendingBindings()
        }
    }

    private class PostDiffUtil : DiffUtil.ItemCallback<PostEntity>(){
        override fun areItemsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean {
              return oldItem == newItem
        }


    }

}





