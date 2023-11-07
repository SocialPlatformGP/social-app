package com.gp.chat.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.gp.chat.model.RecentChat
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters

class ChatAdapter(var onItemClickListener: OnItemClickListener) : ListAdapter<RecentChat,ChatAdapter.ChatViewHolder>(DiffUtilCallBack) {

    inner class ChatViewHolder(val binding:ChatitemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(chat: RecentChat){
            binding.chat=chat
            binding.executePendingBindings()
            itemView.setOnClickListener{
                onItemClickListener.onClick(chat.id!!)

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
}
object DiffUtilCallBack: DiffUtil.ItemCallback<RecentChat>(){
    override fun areItemsTheSame(oldItem: RecentChat, newItem: RecentChat): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: RecentChat, newItem: RecentChat): Boolean {
        return oldItem==newItem
    }

}