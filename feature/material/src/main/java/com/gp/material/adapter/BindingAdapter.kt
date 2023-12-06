package com.gp.material.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gp.material.R

@BindingAdapter("material:imageUrl")
fun setProfilePicture(view: ImageView, picUrl: String?) {
    if (picUrl != null) {
        Glide.with(view.context)
            .load(picUrl)
            .placeholder(R.drawable.img_1)
            .apply(RequestOptions.circleCropTransform())
            .into(view)
    } else {
        view.setImageResource(R.drawable.img_1)
    }
}