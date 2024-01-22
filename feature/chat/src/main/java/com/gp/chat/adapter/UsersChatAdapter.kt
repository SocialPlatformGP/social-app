package com.gp.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gp.chat.R
import com.gp.chat.listener.OnUserClickListener
import com.gp.users.model.User

class UsersChatAdapter(var onUserClickListener: OnUserClickListener): ListAdapter<User,UsersChatAdapter.UsersViewHolder>(UserChatDiffUtill) {

    inner class UsersViewHolder(item: View):RecyclerView.ViewHolder(item){
        val name=item.findViewById<TextView>(R.id.name)
        val  message=item.findViewById<TextView>(R.id.message)
        val img= item.findViewById<ImageView>(R.id.profileImage)
        fun bind(userEntity: User){
            name.text="${userEntity.firstName} ${userEntity.lastName}"
            img.setProfilePicture(userEntity.profilePictureURL)
            itemView.setOnClickListener{
                onUserClickListener.onUserClicked(userEntity.email)
            }

        }

    }


    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val item=getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val  view= LayoutInflater.from(parent.context).inflate(R.layout.chatitem,parent,false)
        return UsersViewHolder(view)
    }

}

object UserChatDiffUtill :DiffUtil.ItemCallback<User>(){
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.email==newItem.email
    }
    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem==newItem
    }

}
