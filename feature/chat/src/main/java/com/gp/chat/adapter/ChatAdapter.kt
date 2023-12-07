package com.gp.chat.adapter


import android.content.Context
import android.system.Os.remove
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.R
import com.gp.chat.databinding.ChatitemBinding
import com.gp.chat.model.Message
import com.gp.chat.listener.OnItemClickListener
import com.gp.chat.listener.OnRecentChatClicked
import com.gp.chat.model.RecentChat
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters

class ChatAdapter(var onItemClickListener: OnRecentChatClicked) :
    ListAdapter<RecentChat, ChatAdapter.ChatViewHolder>(DiffUtilCallBack) {

    inner class ChatViewHolder(val binding: ChatitemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(chat: RecentChat) {
            binding.chat = chat
            val currentUser = Firebase.auth.currentUser?.displayName.toString()
            if(chat.privateChat) {
                if (chat.senderName == currentUser) {
                    binding.name.text = chat.receiverName
                    loadWithGlide(binding.profileImage, chat.receiverPicUrl)
                } else {
                    binding.name.text = chat.senderName
                    loadWithGlide(binding.profileImage, chat.senderPicUrl)
                }
            } else {
                binding.name.text = chat.title
                loadWithGlide(binding.profileImage, chat.senderPicUrl)
            }



            binding.executePendingBindings()

            itemView.setOnLongClickListener {
                if (!chat.privateChat) {
                    createPopUpMenu(itemView, chat)
                }
                true
            }

            itemView.setOnClickListener {
                onItemClickListener.onRecentChatClicked(
                    recentChat = chat,

                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.chatitem,
            parent,
            false
        ) as ChatitemBinding
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun createPopUpMenu(item: View, chat: RecentChat) {

        val chatId = chat.id
        val popupMenu = PopupMenu(item.context, item)
        popupMenu.menuInflater.inflate(R.menu.leavegroup, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener {
            onItemClickListener.leaveGroup(chatId)
            true
        }
        popupMenu.show()
    }

    private fun loadWithGlide(view: ImageView, url: String, isCircular: Boolean = true) {
        Glide.with(view.context).load(url).into(view)
        var requestBuilder = Glide.with(view.context).load(url)
        if (isCircular) {
            requestBuilder = requestBuilder.transform(CircleCrop())
        }
        requestBuilder.into(view)
    }
}


object DiffUtilCallBack : DiffUtil.ItemCallback<RecentChat>() {
    override fun areItemsTheSame(oldItem: RecentChat, newItem: RecentChat): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RecentChat, newItem: RecentChat): Boolean {
        return oldItem == newItem
    }

}