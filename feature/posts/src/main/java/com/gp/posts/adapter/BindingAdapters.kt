package com.gp.posts.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.gp.posts.R
import com.gp.socialapp.util.PostState
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


data class StateWIthLifeCycle(
    var state: StateFlow<State<List<PostEntity>>>,
    var lifecycle: Lifecycle

)

@BindingAdapter("posts:visabilityStatusLoading")
fun setVisability(view: View, params: StateWIthLifeCycle) {
    val status = params.state
    val lifecycle = params.lifecycle
    GlobalScope.launch(Dispatchers.Main) {
        status.flowWithLifecycle(lifecycle).collect { currentState ->
            when (currentState) {
                is State.Loading -> {
                    view.visibility = View.VISIBLE
                }

                else -> {
                    view.visibility = View.GONE
                }
            }

        }

    }

}

@BindingAdapter("posts:visabilityStatusRecycler")
fun setVisabilityRecycler(view: View, params: StateWIthLifeCycle) {
    val status = params.state
    val lifecycle = params.lifecycle
    GlobalScope.launch(Dispatchers.Main) {
        val tag = State.Loading
        status.flowWithLifecycle(lifecycle).collect { currentState ->
            Log.d("TAG", "setVisabilityRecycler: $currentState")
            if (tag == currentState) {
            } else {
                when (currentState) {
                    is State.Loading -> {
                        view.visibility = View.GONE
                        tag == currentState
                    }

                    else -> {
                        view.visibility = View.VISIBLE
                        tag == currentState
                    }
                }

            }
        }

    }
}
@BindingAdapter("app:imageUrl")
fun setProfilePicture(view: ImageView, picUrl: String?) {
    if (picUrl != null) {
        Glide.with(view.context)
            .load(picUrl)
            .placeholder(R.drawable.ic_person_24)
            .apply(RequestOptions.circleCropTransform())
            .into(view)
    } else {
        view.setImageResource(R.drawable.ic_person_24)
    }
}