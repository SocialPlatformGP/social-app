package com.gp.posts.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gp.posts.databinding.ItemFileAttachmentBinding
import com.gp.posts.listeners.OnFileClickedListener
import com.gp.socialapp.database.model.PostAttachment

class FileAttachmentAdapter (
    private val onFileClicked: OnFileClickedListener,
    private val context: Context): ListAdapter<PostAttachment, FileAttachmentAdapter.FileAttachmentViewHolder>(AttachmentDiffUtil()) {
    inner class FileAttachmentViewHolder(private val binding: ItemFileAttachmentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(attachment: PostAttachment){
            Log.d("seerde", "bind called: ${attachment.name}")
            binding.attachment = attachment
            binding.context = context
            binding.root.setOnClickListener{
                onFileClicked.onFileClicked(attachment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileAttachmentViewHolder {
        val binding = ItemFileAttachmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FileAttachmentViewHolder(binding)
    }

    override fun submitList(list: MutableList<PostAttachment>?) {
        Log.d("seerde", "attachments: ${list}")
        super.submitList(list)
    }

    override fun onBindViewHolder(holder: FileAttachmentViewHolder, position: Int) {
        val attachment = getItem(position)
        Log.d("seerde", "data to file attachment rv: ${attachment.name}")
        holder.bind(attachment)
    }

}