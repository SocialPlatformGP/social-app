package com.gp.material.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gp.material.R
import com.gp.material.model.FileType
import com.gp.material.model.MaterialItem

@BindingAdapter("material:imageUrl")
fun setMaterialItemImage(view: ImageView, item: MaterialItem) {
    when (item.fileType) {
        FileType.IMAGE -> {
            if (item.fileUrl != null) {
                Glide.with(view.context)
                    .load(item.fileUrl)
                    .placeholder(R.drawable.img_1)
                    .apply(RequestOptions.circleCropTransform())
                    .into(view)
            } else {
                view.setImageResource(R.drawable.img_1)
            }
        }
        FileType.PDF -> {
            view.setImageResource(R.drawable.img_2)
        }
        FileType.FOLDER -> {
            view.setImageResource(R.drawable.img_6)
        }
        FileType.AUDIO -> {
            view.setImageResource(R.drawable.img_4)
        }
        FileType.VIDEO -> {
            view.setImageResource(R.drawable.img_5)
        }
        else -> {
            view.setImageResource(R.drawable.img_3)
        }
    }
}

