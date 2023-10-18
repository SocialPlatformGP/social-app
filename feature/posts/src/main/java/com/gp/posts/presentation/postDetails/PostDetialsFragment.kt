package com.gp.posts.presentation.postDetails

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.gp.posts.R
import com.gp.posts.adapter.NestedReplyAdapter
import com.gp.posts.databinding.FragmentPostDetialsBinding
import com.gp.posts.listeners.OnAddReplyClicked
import com.gp.posts.listeners.VotePressedListener
import com.gp.posts.presentation.postsfeed.FeedPostViewModel
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NestedReplyItem
import com.gp.socialapp.model.NetworkReply
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetialsFragment : Fragment(), OnAddReplyClicked,VotePressedListener {
    lateinit var replyAdapter: NestedReplyAdapter
    lateinit var recyclerView: RecyclerView
    val viewModel: PostDetailsViewModel by viewModels()
    val feedViewModel: FeedPostViewModel by viewModels()
    val args: PostDetialsFragmentArgs by navArgs()
    lateinit var replyEditText: TextInputEditText
    lateinit var replyEditTextLayout: TextInputLayout
    lateinit var replyButton: ImageButton
    lateinit var linearLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.getRepliesById("xpjgGUTfgW0PY7vGSw7F")
        // Inflate the layout for this fragment
        val binding: FragmentPostDetialsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_post_detials, container, false)
        lifecycleScope.launch {
            viewModel.currentPost.flowWithLifecycle(lifecycle).collect {
                binding.viewModel = viewModel
                binding.postitem = it
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (linearLayout.visibility == View.VISIBLE) {
                    linearLayout.visibility = View.GONE
                } else {
                    findNavController().navigate(R.id.action_postDetialsFragment_to_feedFragment)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
        viewModel.setThePost(args.psot)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        lifecycleScope.launch {
            viewModel.currentReplies.collect {
                replyAdapter = NestedReplyAdapter(this@PostDetialsFragment,0, this@PostDetialsFragment)
                recyclerView.adapter = replyAdapter
                replyAdapter.submitList(it.replies)
            }
        }
        replyEditText = view.findViewById(R.id.et_comment)
        replyEditTextLayout = view.findViewById(R.id.tl_comment)
        replyButton = view.findViewById(R.id.btn_send)
        linearLayout = view.findViewById(R.id.linearLayout)
        val post = view.findViewById<ImageView>(R.id.img_addComment)
        post.setOnClickListener {
            addComment(args.psot)
        }
    }

    private fun addComment(post: PostEntity) {
        replyEditText.requestFocus()
        linearLayout.visibility = View.VISIBLE
        //open keyboard
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(replyEditText, InputMethodManager.SHOW_IMPLICIT)
        replyButton.setOnClickListener {
            viewModel.insertReply(
                NetworkReply(
                    postId = post!!.id,
                    parentReplyId = null,
                    depth = 0,
                    upvotes = 0,
                    downvotes = 0,
                    content = replyEditText.text.toString(),
                )
            )
            replyEditText.setText("")
            replyEditText.clearFocus()
            linearLayout.visibility = View.GONE
            inputMethodManager.hideSoftInputFromWindow(replyEditText.windowToken, 0)
            replyAdapter.notifyDataSetChanged()

        }
    }

    override fun onAddReplyClicked(reply: NestedReplyItem) {
        val currentReply = reply.reply
        replyEditText.requestFocus()
        linearLayout.visibility = View.VISIBLE
        //open keyboard
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(replyEditText, InputMethodManager.SHOW_IMPLICIT)
        replyButton.setOnClickListener {
            viewModel.insertReply(
                NetworkReply(
                    postId = currentReply!!.postId,
                    parentReplyId = currentReply?.id,
                    depth = currentReply!!.depth!!.plus(1),
                    upvotes = 0,
                    downvotes = 0,
                    content = replyEditText.text.toString(),
                )
            )
            replyEditText.setText("")
            replyEditText.clearFocus()
            linearLayout.visibility = View.GONE
            inputMethodManager.hideSoftInputFromWindow(replyEditText.windowToken, 0)
            replyAdapter.notifyDataSetChanged()
        }
    }

    override fun onUpVotePressed(comment: ReplyEntity) {
        viewModel.replyUpVote(comment)
    }

    override fun onDownVotePressed(comment: ReplyEntity) {
        viewModel.replyDownVote(comment)
    }


}