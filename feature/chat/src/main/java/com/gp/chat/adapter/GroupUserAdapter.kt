package com.gp.chat.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gp.chat.R
import com.gp.chat.databinding.ItemGroupMemberBinding
import com.gp.chat.databinding.ItemMemberBinding
import com.gp.chat.listener.OnGroupMembersChangeListener
import com.gp.users.model.SelectableUser
import com.gp.users.model.User

class GroupUserAdapter(private val context: Context,private val onAddGroupMemberListener: OnGroupMembersChangeListener) :  ListAdapter<SelectableUser, GroupUserAdapter.GroupUserViewHolder>(GroupUserDiffCallback()) {
    val TAG = "seerde"
    inner class GroupUserViewHolder(val binding: ItemGroupMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: SelectableUser) {
            Glide.with(context)
                .load(user.data.profilePictureURL)
                .placeholder(R.drawable.baseline_person_24)
                .into(binding.memberAvatarImageview)
            binding.memberNameTextview.text = "${user.data.firstName} ${user.data.lastName}"
            binding.root.setBackgroundColor(
                if(user.isSelected)
                    context.getColor(R.color.selected_color)
                else
                    context.getColor(R.color.unselected_color))
            binding.imageviewCheckmark.visibility = if(user.isSelected) View.VISIBLE else View.GONE
            binding.root.setOnClickListener{
                if(user.isSelected){
                    Log.d(TAG, "User clicked while selected in adapter")
                    onAddGroupMemberListener.onRemoveGroupMember(user.data)


                } else {
                    Log.d(TAG, "User clicked while not selected in adapter")
                    onAddGroupMemberListener.onAddGroupMember(user.data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupUserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemGroupMemberBinding.inflate(layoutInflater, parent, false)
        return GroupUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupUserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
//    fun filterList(list: List<User>, query: String){
//        submitList(list.filter {
//            it.firstName.contains(query, true) || it.lastName.contains(query, true)
//        })
//    }
}

class GroupUserDiffCallback : DiffUtil.ItemCallback<SelectableUser>() {
    override fun areItemsTheSame(oldItem: SelectableUser, newItem: SelectableUser): Boolean {
        return oldItem.data.email == newItem.data.email
    }

    override fun areContentsTheSame(oldItem: SelectableUser, newItem: SelectableUser): Boolean {
        return oldItem == newItem
    }
}
