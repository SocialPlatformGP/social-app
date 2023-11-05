package com.gp.chat.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gp.chat.R
import com.gp.chat.model.Message
import com.gp.chat.listener.OnItemClickListener

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    var arrayList:ArrayList<Message> = arrayListOf()
    var onItemClickListener: OnItemClickListener?=null

    inner class ChatViewHolder(item:View):RecyclerView.ViewHolder(item){
        val name=item.findViewById<TextView>(R.id.name)
        val  message=item.findViewById<TextView>(R.id.message)
        fun bind(chat: Message){
            name.text=chat.senderId.toString()
            message.text=chat.text
            itemView.setOnClickListener{
//                onItemClickListener?.onClick(chat)
            }

        }

    }
    fun setData(arrayList: ArrayList<com.gp.chat.model.Message>){
        this.arrayList=arrayList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val  view= LayoutInflater.from(parent.context).inflate(R.layout.chatitem,parent,false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item=arrayList.get(position)
        holder.bind(item)

    }
}