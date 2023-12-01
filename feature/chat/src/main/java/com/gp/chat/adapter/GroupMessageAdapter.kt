package com.gp.chat.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.R
import com.gp.chat.databinding.ItemReceivedMessageBinding
import com.gp.chat.databinding.ItemSentMessageBinding
import com.gp.chat.listener.OnMessageClickListener
import com.gp.chat.model.Message
import com.gp.chat.presentation.groupchat.GroupChatFragment

class GroupMessageAdapter(private val context: Context, private val messageClickListener: OnMessageClickListener) :
    ListAdapter<Message, GroupMessageAdapter.GroupMessageViewHolder>(GroupMessageDiffCallback()) {
    inner class GroupMessageViewHolder(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {

            if (binding is ItemSentMessageBinding) {
                binding.messageItem = message
            } else if (binding is ItemReceivedMessageBinding) {
                binding.messageItem = message
            }

            itemView.setOnLongClickListener{
                if(message.senderId==Firebase.auth.currentUser?.email){
                    createPopUpMenu(itemView,message)
                }
                 true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            VIEW_TYPE_SENT -> {
                ItemSentMessageBinding.inflate(layoutInflater, parent, false)
            }

            else -> {
                ItemReceivedMessageBinding.inflate(layoutInflater, parent, false)
            }
        }
        return GroupMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupMessageViewHolder, position: Int) {
        val item = getItem(position)
        val previousItem = if (position >= 1) getItem(position - 1) else null
        if (previousItem != null) {
            val isSameSender = previousItem.senderId == item.senderId
            val isDateSame = previousItem.messageDate == item.messageDate
            if (isSameSender && !isCurrentUser(item)) {
                (holder.binding as ItemReceivedMessageBinding).receivedMessageUserpfp.visibility = View.INVISIBLE
                holder.binding.receivedMessageUsernameTextview.visibility = View.GONE
                setTopMargin(holder.binding.root as ConstraintLayout
                , 0)
            }
            if(previousItem.senderId == item.senderId && isCurrentUser(item)){
                setTopMargin((holder.binding as ItemSentMessageBinding).root as ConstraintLayout
                    , 0)
            }
            if (isDateSame) {
                hideDateView(isCurrentUser(item), holder)
            }
        }
        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (isCurrentUser(message)) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    fun isCurrentUser(item: Message): Boolean {
        return item.senderId == Firebase.auth.currentUser?.email
    }

    fun hideDateView(isCurrentUser: Boolean, holder: GroupMessageViewHolder) {
        val dateView = when {
            isCurrentUser -> (holder.binding as ItemSentMessageBinding).sentChatDateTextview
            else -> (holder.binding as ItemReceivedMessageBinding).receivedChatDateTextview
        }
        dateView.visibility = View.GONE
    }
    fun setTopMargin(view: ConstraintLayout, newValue: Int){
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        val newTopMarginInPixels = (newValue * context.resources.displayMetrics.density).toInt()
        layoutParams.topMargin = newTopMarginInPixels
        view.layoutParams = layoutParams
    }
    fun createPopUpMenu(item:View,message: Message){
        Log.d("deleteFun", "createPopUpMenu: $message ")
        val messageId=message.id
        val popupMenu = PopupMenu(item.context,item)
        popupMenu.menuInflater.inflate(R.menu.message_option,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener{ item ->
            when(item.itemId) {
                R.id.menu_item_update->{
                    messageClickListener.updateMessage(messageId,message.groupId,message.message)

                }
                R.id.menu_item_delete->{

                    messageClickListener.deleteMessage(messageId,message.groupId)
                }
            }
            true
        }
        popupMenu.show()
    }

}


class GroupMessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}