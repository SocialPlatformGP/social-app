package com.gp.posts.adapter

import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.gp.posts.R
import com.gp.socialapp.model.Post
import com.gp.socialapp.util.ToTimeTaken
import com.gp.socialapp.utils.State
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

val currentEmail = FirebaseAuth.getInstance().currentUser?.email

data class StateWIthLifeCycle(
    var state: StateFlow<State<List<Post>>>,
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

@BindingAdapter("posts:imageUrl")
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

@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("posts:timeTillNow")
fun setTimeTillNow(view: TextView, time: String?) {
    view.text= ToTimeTaken.calculateTimeDifference(time!!)
    val job=GlobalScope.launch(Dispatchers.Default) {
        repeat (60) {
            delay(60000)
            withContext(Dispatchers.Main) {
                view.text= ToTimeTaken.calculateTimeDifference(time!!)
            }
        }
    }
    job.cancel()
}

@BindingAdapter("posts:upVoteImage")
fun setUpVoteImage(view: MaterialButton, upVoteList: List<String>) {
    if (currentEmail in upVoteList) {
        view.iconTint = view.context.getColorStateList(R.color.Blue)
    } else {
        view.iconTint = view.context.getColorStateList(R.color.Gray)
    }
}

@BindingAdapter("posts:downVoteImage")
fun setDownVoteImage(view: MaterialButton, downVoteList: List<String>) {
    if (currentEmail in downVoteList) {
        view.iconTint = view.context.getColorStateList(R.color.Red)
    } else {
        view.iconTint = view.context.getColorStateList(R.color.Gray)
    }
}
@BindingAdapter(value = ["posts:formattedNumber", "posts:formattedLabel"], requireAll = true)
fun TextView.setFormattedNumberWithLabel(number: Int, label: String) {
    val suffixes = arrayOf("", "k", "M", "B", "T")
    var num = number.toDouble()
    var suffixIndex = 0

    while (num >= 1000 && suffixIndex < suffixes.size - 1) {
        num /= 1000
        suffixIndex++
    }

    val formattedNumber = String.format("%.1f", num)
    val formattedText = "$formattedNumber${suffixes[suffixIndex]} $label"
    text = formattedText
}
