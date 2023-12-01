package com.gp.chat.adapter


import android.content.Context
import android.system.Os.remove
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.R
import com.gp.chat.databinding.ChatitemBinding
import com.gp.chat.model.Message
import com.gp.chat.listener.OnItemClickListener
import com.gp.chat.listener.OnRecentChatClicked
import com.gp.chat.model.RecentChat
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters

class ChatAdapter(var onItemClickListener: OnRecentChatClicked) : ListAdapter<RecentChat,ChatAdapter.ChatViewHolder>(DiffUtilCallBack) {

    inner class ChatViewHolder(val binding:ChatitemBinding):RecyclerView.ViewHolder(binding.root){



        fun bind(chat: RecentChat){
            binding.chat=chat
            binding.executePendingBindings()
            itemView.setOnLongClickListener {
                createPopUpMenu(itemView,chat)
                true }

            itemView.setOnClickListener{
                onItemClickListener.onRecentChatClicked(
                    chatId = chat.id,
                    receiverName = chat.receiverName,
                    senderName = chat.senderName,
                    isPrivateChat = chat.privateChat
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val  binding=DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.chatitem,
            parent,
            false
        ) as ChatitemBinding
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item=getItem(position)
        holder.bind(item)
    }

    fun createPopUpMenu(item:View,chat:RecentChat){
        val chatId=chat.id
        val popupMenu = PopupMenu(item.context,item)
        popupMenu.menuInflater.inflate(R.menu.message_option,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener{ item ->
            when(item.itemId) {
                R.id.menu_item_update->{

                }
                R.id.menu_item_delete->{

                }
            }
            true
        }
        popupMenu.show()
    }
        }





object DiffUtilCallBack: DiffUtil.ItemCallback<RecentChat>(){
    override fun areItemsTheSame(oldItem: RecentChat, newItem: RecentChat): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: RecentChat, newItem: RecentChat): Boolean {
        return oldItem==newItem
    }


}