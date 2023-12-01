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
import com.gp.chat.listener.OnGroupMemberClicked
import com.gp.chat.listener.OnGroupMembersChangeListener
import com.gp.users.model.User

class GroupDetailsMemberAdapter(private val context: Context,
    private val onGroupMemberClicked: OnGroupMemberClicked) :  ListAdapter<User, GroupDetailsMemberAdapter.GroupMemberViewHolder>(GroupMemberDiffCallback()) {
    inner class GroupMemberViewHolder(val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            Glide.with(context)
                .load(user.profilePictureURL)
                .placeholder(R.drawable.baseline_person_24)
                .into(binding.memberAvatarImageview)
            binding.memberNameTextview.text = "${user.firstName} ${user.lastName}"
            binding.root.setOnClickListener{
                onGroupMemberClicked.onMemberClicked(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMemberViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMemberBinding.inflate(layoutInflater, parent, false)
        return GroupMemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class GroupMemberDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}