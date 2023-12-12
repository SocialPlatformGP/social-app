package com.gp.chat.utils

import android.app.Dialog
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gp.chat.R
import com.gp.chat.databinding.FragmentFullScreenImageBinding

class FullScreenImageDialogFragment : DialogFragment() {
    lateinit var binding: FragmentFullScreenImageBinding
    private val args: FullScreenImageDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_full_screen_image,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fullScreenImageView = binding.fullScreenImageView
        // Load the image using Glide or any other image loading library

        Glide.with(this)
            .load(args.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(fullScreenImageView)

        fullScreenImageView.setOnClickListener {
            dismiss()
        }
    }
}

