package com.gp.posts.adapter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gp.posts.R
import com.gp.posts.databinding.SuggestItemBinding
import com.gp.posts.listeners.OnSuggestedPostClickListener
import com.gp.socialapp.model.Post

class SearchAdapter(private var onClickListener: OnSuggestedPostClickListener): ListAdapter<Post, SearchAdapter.SuggestViewHolder>(
    PostDiffUtil()
) {

    inner class SuggestViewHolder(private val binding: SuggestItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.suggestitem = post
            binding.executePendingBindings()
        }
    }

    private class PostDiffUtil : DiffUtil.ItemCallback<Post>(){
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }


        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestViewHolder {
        var binding :SuggestItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.suggest_item,
            parent,
            false
        )
        return SuggestViewHolder(binding)
    }



    override fun onBindViewHolder(holder: SuggestViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
        holder.itemView.setOnClickListener {
            Log.d("click", post.toString())
          onClickListener.onClick(post)
        }
    }
}
