package com.gp.chat.adapter

import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.R
import com.gp.chat.databinding.ItemMessageRecieveBinding
import com.gp.chat.databinding.ItemMessageSendBinding
import com.gp.chat.model.Message
import com.gp.chat.util.RemoveSpecialChar
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
import com.gp.chat.util.ToSimpleTimeFormat


class MessageAdapter() : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()){

        inner class MessageViewHolder(private val binding: ViewDataBinding) :RecyclerView.ViewHolder(binding.root){



            fun bind(message: Message){
                if (binding is ItemMessageSendBinding){
                    binding.message = message
                }else if (binding is ItemMessageRecieveBinding){
                    binding.message = message
                }

            }



        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layout = if (viewType == VIEW_TYPE_SENT) {
            ItemMessageSendBinding.inflate(layoutInflater, parent, false)
        } else {
            ItemMessageRecieveBinding.inflate(layoutInflater, parent, false)
        }
        return MessageViewHolder(layout)

    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        var item = getItem(position)
        item=item.copy(timestamp = ToSimpleTimeFormat.convertTimestampStringToFormattedTime(item.timestamp!!))
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderId == removeSpecialCharacters(Firebase.auth.currentUser?.email!!)) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    companion object{
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}

