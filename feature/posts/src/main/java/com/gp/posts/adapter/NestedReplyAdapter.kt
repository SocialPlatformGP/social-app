package com.gp.posts.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.gp.posts.R
import com.gp.posts.listeners.OnAddReplyClicked
import com.gp.posts.listeners.VotePressedListener
import com.gp.posts.listeners.VotesClickedListener
import com.gp.socialapp.model.NestedReplyItem

class NestedReplyAdapter(
    private val votesClickedListener: VotePressedListener,
    private val depth: Int,
    private val onReplyClicked: OnAddReplyClicked
) : ListAdapter<NestedReplyItem, NestedReplyAdapter.NestedReplyViewHolder>(NestedReplyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedReplyViewHolder {
        Log.d("NestedReplyAdapter", "bind: $depth")

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reply, parent, false)
        return NestedReplyViewHolder(view)
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: NestedReplyViewHolder, position: Int) {
        val nestedReplyItem = getItem(position)
        holder.bind(nestedReplyItem)

        holder.replyButton.setOnClickListener {
            onReplyClicked.onAddReplyClicked(nestedReplyItem)
        }

        holder.itemView.setOnClickListener {
            if (holder.defaultView.visibility == View.VISIBLE) {
                holder.nestedRecyclerView.visibility = View.GONE
                holder.defaultView.visibility = View.GONE
                holder.alterView.visibility = View.VISIBLE


            } else {
                holder.defaultView.visibility = View.VISIBLE
                holder.alterView.visibility = View.GONE
                holder.nestedRecyclerView.visibility = View.VISIBLE
            }

        }


        if (nestedReplyItem.reply?.parentReplyId == null) {
            val params = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(0, 0, 0, 18)
            val param = holder.alterView.layoutParams as ViewGroup.MarginLayoutParams
            param.marginEnd = 16
            holder.alterView.layoutParams = param
        } else {
            val params = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(0, 0, 0, 0)
        }

        holder.upVoteImage.setOnClickListener {
            votesClickedListener.onUpVotePressed(nestedReplyItem.reply!!)
        }
        holder.downVoteImage.setOnClickListener {
            votesClickedListener.onDownVotePressed(nestedReplyItem.reply!!)
        }
    }


    inner class NestedReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val replyContent: TextView = itemView.findViewById(R.id.replyTextView)
        val nestedRecyclerView: RecyclerView = itemView.findViewById(R.id.nestedRecyclerView)
        private val indicator: View = itemView.findViewById(R.id.depthIndicator)
        val alterView = itemView.findViewById<LinearLayout>(R.id.alternativeReplyContainer)
        val defaultView: ConstraintLayout = itemView.findViewById(R.id.defultViewNested)
        private val contentAlter: TextView = itemView.findViewById(R.id.content_collapse)
        private val voteText: TextView = itemView.findViewById(R.id.textView4)
        val replyButton: ImageView = itemView.findViewById(R.id.img_addComment)
        val upVoteImage: ImageView = itemView.findViewById(R.id.imageView3)
        val downVoteImage: ImageView = itemView.findViewById(R.id.imageView2)


        fun bind(nestedReplyItem: NestedReplyItem) {
            replyContent.text = nestedReplyItem.reply?.content
            contentAlter.text = nestedReplyItem.reply?.content
            voteText.text = nestedReplyItem.reply?.upvotes.toString()
            if (depth == 0) {
                indicator.visibility = View.GONE
                //add margin button = 12
                val params = itemView.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(0, 0, 0, 32)
            } else {
                indicator.visibility = View.VISIBLE
            }


            // Initialize a new instance of the NestedReplyAdapter with the nested replies
            val adapter = NestedReplyAdapter(votesClickedListener,depth + 1, onReplyClicked)
            adapter.submitList(nestedReplyItem.replies)
            nestedRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            nestedRecyclerView.adapter = adapter
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
