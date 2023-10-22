package com.gp.posts.presentation.postDetails

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.play.core.integrity.p
import com.gp.posts.R
import com.gp.posts.adapter.NestedReplyAdapter
import com.gp.posts.databinding.FragmentPostDetialsBinding
import com.gp.posts.listeners.OnAddReplyClicked
import com.gp.posts.listeners.OnMoreOptionClicked
import com.gp.posts.listeners.OnReplyCollapsed
import com.gp.posts.listeners.VotePressedListener
import com.gp.posts.presentation.postsfeed.FeedFragmentDirections
import com.gp.posts.presentation.postsfeed.FeedPostViewModel
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NestedReplyItem
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Reply
import com.gp.socialapp.util.ToNestedReplies.toNestedReplies
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class PostDetialsFragment
    : Fragment(), OnAddReplyClicked,VotePressedListener,OnMoreOptionClicked, OnReplyCollapsed {
    lateinit var replyAdapter: NestedReplyAdapter
    lateinit var recyclerView: RecyclerView
    val viewModel: PostDetailsViewModel by viewModels()
    val args: PostDetialsFragmentArgs by navArgs()
    lateinit var binding: FragmentPostDetialsBinding
    lateinit var replyEditText: TextInputEditText
    lateinit var replyEditTextLayout: TextInputLayout
    lateinit var replyButton: MaterialButton
    lateinit var linearLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.getRepliesById(args.post.id)
        // Inflate the layout for this fragment
         binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_post_detials, container, false)
        viewModel.getPost(args.post)
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

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.itemAnimator = null
        replyAdapter = NestedReplyAdapter(
            this@PostDetialsFragment,
            0,
            this@PostDetialsFragment,
            this@PostDetialsFragment,
            this@PostDetialsFragment
        )
        recyclerView.adapter = replyAdapter

        lifecycleScope.launch {
            viewModel.currentReplies.collectLatest {
                Log.d("PostDetailsFragment", "onViewCreated: ${it.replies.toString()}")
                replyAdapter.submitList(it.replies)

            }
        }

        replyEditText = view.findViewById(R.id.et_comment)
        replyEditTextLayout = view.findViewById(R.id.tl_comment)
        replyButton = view.findViewById(R.id.btn_send)
        linearLayout = view.findViewById(R.id.linearLayout)
        val post = view.findViewById<MaterialButton>(R.id.img_addComment)
        post.setOnClickListener {
            addComment(args.post)
        }
        binding.moreOptionPost.setOnClickListener {
            onMoreOptionClicked(binding.moreOptionPost,args.post)
        }
        binding.imageViewUpvotePost.setOnClickListener {
            viewModel.upVote(args.post)
        }
        binding.imageViewDownvotePost.setOnClickListener {
            viewModel.downVote(args.post)
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun addComment(post: Post) {
        replyEditText.requestFocus()
        linearLayout.visibility = View.VISIBLE
        //open keyboard
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(replyEditText, InputMethodManager.SHOW_IMPLICIT)
        replyButton.setOnClickListener {
            viewModel.insertReply(
                Reply(
                    postId = post!!.id,
                    parentReplyId = null,
                    depth = 0,
                    content = replyEditText.text.toString(),
                    createdAt =LocalDateTime.now(ZoneId.of("UTC")).toString(),
                )
            )
            replyEditText.setText("")
            replyEditText.clearFocus()
            linearLayout.visibility = View.GONE
            inputMethodManager.hideSoftInputFromWindow(replyEditText.windowToken, 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                Reply(
                    postId = currentReply!!.postId,
                    parentReplyId = currentReply?.id,
                    depth = currentReply!!.depth!!.plus(1),
                    content = replyEditText.text.toString(),
                    createdAt = LocalDateTime.now(ZoneId.of("UTC")).toString(),
                )
            )
            replyEditText.setText("")
            replyEditText.clearFocus()
            linearLayout.visibility = View.GONE
            inputMethodManager.hideSoftInputFromWindow(replyEditText.windowToken, 0)
        }
    }

    override fun onUpVotePressed(comment: Reply) {
        viewModel.replyUpVote(comment)
    }

    override fun onDownVotePressed(comment: Reply) {
        viewModel.replyDownVote(comment)
    }


    override fun onMoreOptionClicked(imageView5: MaterialButton, postitem: Post) {
        val popupMenu = PopupMenu(requireActivity(), imageView5)
        popupMenu.menuInflater.inflate(R.menu.extra_option_menu, popupMenu.menu)

        // Set item click listener for the popup menu
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_save -> {
                    // Handle Item 1 click
                    // Add your code here
                    true
                }
                R.id.item_delete -> {
                    viewModel.deletePost(postitem)
                    true
                }
                R.id.item_report -> {
                    true
                }
                R.id.item_edit -> {
                    val action = PostDetialsFragmentDirections.actionPostDetialsFragmentToEditPostFragment(postitem)
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }
        // Show the popup menu
        popupMenu.show()
    }

    override fun onMoreOptionClicked(imageView5: MaterialButton, reply: Reply) {
        val popupMenu = PopupMenu(requireActivity(), imageView5)
        popupMenu.menuInflater.inflate(R.menu.extra_option_menu, popupMenu.menu)

        // Set item click listener for the popup menu
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_delete -> {
                    viewModel.deleteReply(reply)
                    true
                }
                R.id.item_edit -> {
                    replyEditText.requestFocus()
                    linearLayout.visibility = View.VISIBLE
                    //open keyboard
                    val inputMethodManager =
                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(replyEditText, InputMethodManager.SHOW_IMPLICIT)
                    replyEditText.setText(reply.content)
                    replyButton.setOnClickListener {
                        viewModel.updateReply(
                            reply.copy(
                                content = replyEditText.text.toString(),
                                editStatus = true
                            )
                        )
                        replyEditText.setText("")
                        replyEditText.clearFocus()
                        linearLayout.visibility = View.GONE
                        inputMethodManager.hideSoftInputFromWindow(replyEditText.windowToken, 0)
                    }

                    true
                }
                R.id.item_report -> {
                    //todo after ml model feature
                    true
                }
                R.id.item_save -> {
                    //todo after bookmark feature
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onReplyCollapsed(reply: Reply) {
        if(reply.collapsed){
            viewModel.collapsedReplies.remove(reply.id)
        }else {
            viewModel.collapsedReplies.add(reply.id)
        }
        Log.d("zarea in fragment",viewModel.collapsedReplies.toString())
    }


}