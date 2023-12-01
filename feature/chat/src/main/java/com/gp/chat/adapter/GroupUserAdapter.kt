package com.gp.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gp.chat.R
import com.gp.chat.databinding.ItemMemberBinding
import com.gp.chat.listener.OnGroupMembersChangeListener
import com.gp.users.model.User

class GroupUserAdapter(private val context: Context,private val onAddGroupMemberListener: OnGroupMembersChangeListener) :  ListAdapter<User, GroupUserAdapter.GroupUserViewHolder>(GroupUserDiffCallback()) {
    inner class GroupUserViewHolder(val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            Glide.with(context)
                .load(user.profilePictureURL)
                .placeholder(R.drawable.baseline_person_24)
                .into(binding.memberAvatarImageview)
            binding.memberNameTextview.text = "${user.firstName} ${user.lastName}"
            binding.root.setOnClickListener{
                onAddGroupMemberListener.onAddGroupMember(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupUserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMemberBinding.inflate(layoutInflater, parent, false)
        return GroupUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupUserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    fun filterList(list: List<User>, query: String){
        submitList(list.filter {
            it.firstName.contains(query, true) || it.lastName.contains(query, true)
        })
    }
}

class GroupUserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}