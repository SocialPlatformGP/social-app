package com.gp.posts.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.google.android.material.button.MaterialButton
import com.gp.posts.R
import com.gp.posts.databinding.ItemReplyBinding
import com.gp.posts.listeners.OnAddReplyClicked
import com.gp.posts.listeners.OnMoreOptionClicked
import com.gp.posts.listeners.OnReplyCollapsed
import com.gp.posts.listeners.VotePressedListener
import com.gp.posts.listeners.VotesClickedListener
import com.gp.socialapp.model.NestedReplyItem
import com.gp.socialapp.util.ToTimeTaken.calculateTimeDifference
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NestedReplyAdapter(
    private val votesClickedListener: VotePressedListener,
    private val depth: Int,
    private val onReplyClicked: OnAddReplyClicked,
    private val onMoreOptionClicked: OnMoreOptionClicked,
    private val onReplyCollapsed: OnReplyCollapsed
) : ListAdapter<NestedReplyItem, NestedReplyAdapter.NestedReplyViewHolder>(NestedReplyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedReplyViewHolder {
        val binding: ItemReplyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_reply,
            parent,
            false
        )
        return NestedReplyViewHolder(binding)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NestedReplyViewHolder, position: Int) {
        val nestedReplyItem = getItem(position)
        holder.bind(nestedReplyItem)

    }


    inner class NestedReplyViewHolder(private var binding: ItemReplyBinding) : RecyclerView.ViewHolder(binding.root) {
        private val nestedRecyclerView = binding.nestedRecyclerView
        private val indicator = binding.depthIndicator
        private val alterView = binding.alternativeReplyContainer
        private val defaultView = binding.defultViewNested
        private val time = binding.textTime
        private val replyButton = binding.imgAddComment
        private val upVoteImage = binding.imageView3
        private val downVoteImage = binding.imageView2
        private val moreOptionButton = binding.imageView5

        @OptIn(DelicateCoroutinesApi::class)
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(nestedReplyItem: NestedReplyItem) {
            //bind the reply to the view
            binding.reply = nestedReplyItem.reply
            binding.executePendingBindings()
            //prevent the reply from being clicked if it is deleted
            if (nestedReplyItem.reply?.deleted == true) {
                defaultView.visibility = View.GONE
                alterView.visibility = View.VISIBLE
            }
            else {
                defaultView.visibility = View.VISIBLE
                alterView.visibility = View.GONE
            }

            if (nestedReplyItem.reply?.collapsed == true) {
                defaultView.visibility = View.GONE
                nestedRecyclerView.visibility = View.GONE
                alterView.visibility = View.VISIBLE
            }
            else {
                defaultView.visibility = View.VISIBLE
                alterView.visibility = View.GONE
                nestedRecyclerView.visibility = View.VISIBLE

            }
            // put margin button = 12 if the reply is the last reply in the nested replies
            if (depth == 0) {
                indicator.visibility = View.GONE
                //add margin button = 12
                val params = itemView.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(0, 0, 0, 32)
            }
            else {
                indicator.visibility = View.VISIBLE
            }

            //to add comment
            replyButton.setOnClickListener {
                onReplyClicked.onAddReplyClicked(nestedReplyItem)
            }
            //handle collapse and expand
            itemView.setOnClickListener {
                if (defaultView.visibility == View.VISIBLE && nestedReplyItem.reply?.deleted == false) {
                    nestedRecyclerView.visibility = View.GONE
                    defaultView.visibility = View.GONE
                    alterView.visibility = View.VISIBLE
                    onReplyCollapsed.onReplyCollapsed(nestedReplyItem.reply!!.copy(collapsed = true))

                } else if (defaultView.visibility == View.GONE && nestedReplyItem.reply?.deleted == false) {
                    defaultView.visibility = View.VISIBLE
                    alterView.visibility = View.GONE
                    nestedRecyclerView.visibility = View.VISIBLE
                    onReplyCollapsed.onReplyCollapsed(nestedReplyItem.reply!!.copy(collapsed = false))

                } else {
                    //if the reply is deleted
                    defaultView.visibility = View.GONE
                    alterView.visibility = View.VISIBLE
                }

            }

            //upvote and downvote button
            upVoteImage.setOnClickListener {
                votesClickedListener.onUpVotePressed(nestedReplyItem.reply!!)
            }
            downVoteImage.setOnClickListener {
                votesClickedListener.onDownVotePressed(nestedReplyItem.reply!!)
            }
            //more option button
            moreOptionButton.setOnClickListener {
                onMoreOptionClicked.onMoreOptionClicked(
                    moreOptionButton,
                    nestedReplyItem.reply!!
                )
            }

            // Initialize a new instance of the NestedReplyAdapter with the nested replies
            val adapter = NestedReplyAdapter(
                votesClickedListener,
                depth + 1,
                onReplyClicked,
                onMoreOptionClicked,
                onReplyCollapsed
            )
            //submit the nested replies to the adapter
            adapter.submitList(nestedReplyItem.replies)
            nestedRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            nestedRecyclerView.adapter = adapter
            nestedRecyclerView.itemAnimator = null

        }
    }

    class NestedReplyDiffUtil() : DiffUtil.ItemCallback<NestedReplyItem>() {
        override fun areItemsTheSame(oldItem: NestedReplyItem, newItem: NestedReplyItem): Boolean {
            return oldItem.reply?.id == newItem.reply?.id
        }

        override fun areContentsTheSame(
            oldItem: NestedReplyItem,
            newItem: NestedReplyItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}
