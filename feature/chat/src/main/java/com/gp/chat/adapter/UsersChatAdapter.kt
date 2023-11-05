package com.gp.chat.adapter

import android.media.browse.MediaBrowser.ItemCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gp.chat.R
import com.gp.chat.listener.OnItemClickListener
import com.gp.socialapp.database.model.UserEntity

class UsersChatAdapter(var onItemClickListener: OnItemClickListener): ListAdapter<UserEntity,UsersChatAdapter.UsersViewHolder>(UserChatDiffUtill) {

    inner class UsersViewHolder(item: View):RecyclerView.ViewHolder(item){
        val name=item.findViewById<TextView>(R.id.name)
        val  message=item.findViewById<TextView>(R.id.message)
        val img= item.findViewById<ImageView>(R.id.profileImage)
        fun bind(userEntity: UserEntity){
            name.text="${userEntity.userFirstName} ${userEntity.userLastName}"
            img.setProfilePicture(userEntity.userProfilePictureURL)
            itemView.setOnClickListener{
                onItemClickListener.onClick(userEntity)
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

object UserChatDiffUtill :DiffUtil.ItemCallback<UserEntity>(){
    override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
        return oldItem.userEmail==newItem.userEmail
    }
    override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
        return oldItem==newItem
    }

}
