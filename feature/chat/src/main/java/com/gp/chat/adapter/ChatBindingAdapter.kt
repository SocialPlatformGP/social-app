package com.gp.chat.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gp.chat.R

@BindingAdapter("users:imageUrl")
fun ImageView.setProfilePicture(picUrl: String?) {
    if (picUrl != null) {
        Glide.with(this.context)
            .load(picUrl)
            .placeholder(R.drawable.ic_person_24)
            .apply(RequestOptions.circleCropTransform())
            .into(this)
    } else {
        this.setImageResource(R.drawable.ic_person_24)
    }
}