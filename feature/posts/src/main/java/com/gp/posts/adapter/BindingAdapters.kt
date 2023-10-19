package com.gp.posts.adapter

import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.gp.socialapp.util.PostState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class StateWIthLifeCycle(
    var postState: StateFlow<PostState>,
    var lifecycle: Lifecycle

)

@BindingAdapter("posts:visabilityStatusLoading")
fun setVisability(view: View, params: StateWIthLifeCycle) {
    val status = params.postState
    val lifecycle = params.lifecycle
    GlobalScope.launch(Dispatchers.Main) {
        status.flowWithLifecycle(lifecycle).collect { currentState ->
            when (currentState) {
                is PostState.Loading -> {
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
    val status = params.postState
    val lifecycle = params.lifecycle
    GlobalScope.launch(Dispatchers.Main) {
        status.flowWithLifecycle(lifecycle).collect { currentState ->
            Log.d("TAG", "setVisabilityRecycler: $currentState")
            when (currentState) {
                is PostState.Loading -> {
                    view.visibility = View.GONE
                }

                else -> {
                    view.visibility = View.VISIBLE
                }
            }

        }

    }
}



@BindingAdapter("android:textWatcher")
fun bindTextWatcher(view: EditText, textWatcher: TextWatcher?) {
    if (textWatcher != null) {
        view.addTextChangedListener(textWatcher)
    } else {
        view.addTextChangedListener(null)
    }
}
