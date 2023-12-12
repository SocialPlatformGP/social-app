package com.gp.posts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gp.posts.databinding.ItemImageAttachmentBinding
import com.gp.socialapp.database.model.PostAttachment

class ImageAttachmentAdapter(attachments: List<PostAttachment>): ListAdapter<PostAttachment, ImageAttachmentAdapter.ImageAttachmentViewHolder>(AttachmentDiffUtil()) {
    init{
        submitList(attachments)
    }
    inner class ImageAttachmentViewHolder(private val binding: ItemImageAttachmentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(attachment: PostAttachment){
            binding.attachment = attachment
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAttachmentViewHolder {
        val binding = ItemImageAttachmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageAttachmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageAttachmentViewHolder, position: Int) {
        val attachment = getItem(position)
        holder.bind(attachment)
    }

}
class AttachmentDiffUtil : DiffUtil.ItemCallback<PostAttachment>(){
    override fun areItemsTheSame(oldItem: PostAttachment, newItem: PostAttachment): Boolean {
        return oldItem.url == newItem.url
    }
    override fun areContentsTheSame(oldItem: PostAttachment, newItem: PostAttachment): Boolean {
        return oldItem == newItem
    }
}