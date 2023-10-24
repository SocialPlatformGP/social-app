package com.gp.posts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gp.posts.R
import com.gp.posts.databinding.ItemPostBinding
import com.gp.posts.databinding.SearchItemBinding
import com.gp.posts.listeners.OnMoreOptionClicked
import com.gp.posts.listeners.VotesClickedListener
import com.gp.socialapp.database.model.PostEntity

class SearchResultAdapter () : ListAdapter<UiPost, SearchResultAdapter.SearchResultViewHolder>(PostDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding : SearchItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.search_item,
            parent,
            false
        )
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }


    inner class SearchResultViewHolder(private val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: UiPost) {
            binding.post = post
            binding.executePendingBindings()
        }
    }

    private class PostDiffUtil : DiffUtil.ItemCallback<UiPost>(){
        override fun areItemsTheSame(oldItem: UiPost, newItem: UiPost): Boolean {
            return oldItem.publishTime == newItem.publishTime && oldItem.authorName == newItem.authorName
        }

        override fun areContentsTheSame(oldItem: UiPost, newItem: UiPost): Boolean {
            return oldItem == newItem
        }

    }

}
data class UiPost(
    val pictureUrl: String,
    val authorName: String,
    val publishTime: String,
    val title: String,
    val content: String,
    val upvoteCount: Int,
    val commentCount: Int
)