package com.gp.chat.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.gp.chat.R
import com.gp.chat.listener.OnGroupMembersChangeListener
import com.gp.users.model.User

@BindingAdapter("chat:imageUrl")
fun setProfilePicture(view: ImageView, picUrl: String?) {
    if (picUrl != null) {
        Glide.with(view.context)
            .load(picUrl)
            .placeholder(R.drawable.baseline_person_24)
            .apply(RequestOptions.circleCropTransform())
            .into(view)
    } else {
        view.setImageResource(R.drawable.baseline_person_24)
    }
}
@BindingAdapter(value = ["chat:members", "chat:membersContext", "chat:onMembersChange"], requireAll = true)
fun setSelectedMembers(view: ChipGroup, selectedMembers: List<User>, context: Context, onGroupMembersChangeListener: OnGroupMembersChangeListener) {
    view.removeAllViews()
    if (view.childCount==0) {
        selectedMembers.forEach {user ->
            val label = user.firstName
            val chip = Chip(context)
            chip.text = label
            chip.textSize = 14f
            Glide.with(context)
                .load(user.profilePictureURL)
                .override(48, 48)
                .centerCrop()
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        chip.chipIcon = resource
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                view.removeView(chip)
                onGroupMembersChangeListener.onRemoveGroupMember(user)
            }
            chip.shapeAppearanceModel
                .toBuilder()
                .setAllCornerSizes(64f)
                .build()
            view.addView(chip)
        }
    }
}