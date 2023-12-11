package com.gp.posts.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.gp.posts.R
import com.gp.posts.databinding.ItemPostBinding
import com.gp.posts.listeners.OnFileClickedListener
import com.gp.posts.listeners.OnMoreOptionClicked
import com.gp.posts.listeners.OnTagClicked
import com.gp.posts.listeners.VotesClickedListenerPost
import com.gp.socialapp.database.model.MimeType
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.model.Post

class FeedPostAdapter(
    val onMoreOptionClicked: OnMoreOptionClicked,
    val onPostClicked: VotesClickedListenerPost,
    val onTagClicked: OnTagClicked,
    val onFileClicked: OnFileClickedListener,
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
            binding.onTagClick = onTagClicked
            if(post.attachments.isNotEmpty()){
                bindAttachments(post.attachments)
            }
            binding.executePendingBindings()
        }

        private fun bindAttachments(attachments: List<PostAttachment>) {
            binding.mediaFramelayout.visibility = View.VISIBLE
            val IMAGE_TYPES = listOf(
                MimeType.JPEG.readableType,
                MimeType.PNG.readableType,
                MimeType.GIF.readableType,
                MimeType.BMP.readableType,
                MimeType.TIFF.readableType,
                MimeType.WEBP.readableType,
                MimeType.SVG.readableType
                )

            val VIDEO_TYPES = listOf(
                MimeType.MP4.readableType,
                MimeType.AVI.readableType,
                MimeType.MKV.readableType,
                MimeType.MOV.readableType,
                MimeType.WMV.readableType
            )
            when(attachments.first().type){
                in IMAGE_TYPES -> {
                    bindImageAttachments(attachments)
                }
                in VIDEO_TYPES ->{
                    bindVideoAttachments(attachments)
                }
                else -> {
                    bindFileAttachments(attachments)
                }
            }
        }


        @SuppressLint("ClickableViewAccessibility")
        private fun bindImageAttachments(attachments: List<PostAttachment>) {
            binding.mediaRecyclerview.visibility = View.GONE
            binding.mediaViewpager.visibility = View.VISIBLE
            binding.springDotsIndicator.visibility = View.VISIBLE
            binding.mediaViewpager.adapter =  ImageAttachmentAdapter(attachments)
            binding.mediaViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.springDotsIndicator.attachTo(binding.mediaViewpager)
        }

        private fun bindVideoAttachments(attachments: List<PostAttachment>) {
            //TODO: bind video attachments
            bindFileAttachments(attachments)
        }

        private fun bindFileAttachments(attachments: List<PostAttachment>) {
            binding.mediaRecyclerview.visibility = View.VISIBLE
            binding.mediaViewpager.visibility = View.GONE
            binding.springDotsIndicator.visibility = View.GONE
            val adapter = FileAttachmentAdapter(onFileClicked, context)
            adapter.submitList(attachments.toMutableList())
            binding.mediaRecyclerview.adapter = adapter
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





