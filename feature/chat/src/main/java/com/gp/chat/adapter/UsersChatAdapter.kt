package com.gp.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gp.chat.R
import com.gp.chat.listener.OnItemClickListener
import com.gp.socialapp.database.model.UserEntity

class UsersChatAdapter: RecyclerView.Adapter<UsersChatAdapter.UsersViewHolder>() {
    var arrayList:ArrayList<UserEntity> = arrayListOf()
    var onItemClickListener: OnItemClickListener?=null

    inner class UsersViewHolder(item: View):RecyclerView.ViewHolder(item){
        val name=item.findViewById<TextView>(R.id.name)
        val  message=item.findViewById<TextView>(R.id.message)
        val img= item.findViewById<ImageView>(R.id.profileImage)
        fun bind(userEntity: UserEntity){
            name.text="${userEntity.userFirstName} ${userEntity.userLastName}"
            img.setProfilePicture(userEntity.userProfilePictureURL)

        }

    }
    fun setData(arrayList: ArrayList<UserEntity>){
        this.arrayList=arrayList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val item=arrayList.get(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val  view= LayoutInflater.from(parent.context).inflate(R.layout.chatitem,parent,false)
        return UsersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


}
