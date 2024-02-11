package com.gp.posts.presentation.postDetails

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.posts.R
import com.gp.posts.adapter.FileAttachmentAdapter
import com.gp.posts.adapter.ImageAttachmentAdapter
import com.gp.posts.adapter.NestedReplyAdapter
import com.gp.posts.databinding.FragmentPostDetailsBinding
import com.gp.posts.listeners.OnAddReplyClicked
import com.gp.posts.listeners.OnFileClickedListener
import com.gp.posts.listeners.OnMoreOptionClicked
import com.gp.posts.listeners.OnReplyCollapsed
import com.gp.posts.listeners.OnTagClicked
import com.gp.posts.listeners.VotePressedListener
import com.gp.socialapp.database.model.MimeType
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.model.NestedReplyItem
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Reply
import com.gp.socialapp.model.Tag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.internal.cache2.Relay.Companion.edit
import java.util.Date



@AndroidEntryPoint
class PostDetailsFragment
    : Fragment()//, OnAddReplyClicked,VotePressedListener,OnMoreOptionClicked, OnReplyCollapsed,OnTagClicked
    {
//    lateinit var replyAdapter: NestedReplyAdapter
//    lateinit var recyclerView: RecyclerView
       val viewModel: PostDetailsViewModel by viewModels()
   val args: PostDetailsFragmentArgs by navArgs()
//    lateinit var binding: FragmentPostDetailsBinding
//    lateinit var replyEditText: TextInputEditText
//    lateinit var replyEditTextLayout: TextInputLayout
//    lateinit var replyButton: MaterialButton
//    lateinit var linearLayout: LinearLayout
//    private val currentUser= Firebase.auth.currentUser
        lateinit var composeView: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.getRepliesById(args.post.id)
        // Inflate the layout for this fragment
//         binding =
//            DataBindingUtil.inflate(inflater, R.layout.fragment_post_details, container, false)
//        viewModel.getPost(args.post)
//
//        lifecycleScope.launch {
//            viewModel.currentPost.flowWithLifecycle(lifecycle).collect {
//                binding.viewModel = viewModel
//                binding.postitem = it
//                binding.context = requireContext()
//               // binding.onTagClick = this@PostDetailsFragment
//            }
        ComposeView(requireContext()).also {
            composeView=it
        }
        return composeView
//        }
//
//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if (linearLayout.visibility == View.VISIBLE) {
//                    linearLayout.visibility = View.GONE
//                } else {
//                    findNavController().navigate(R.id.action_postDetialsFragment_to_feedFragment)
//                }
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
//
//        binding.lifecycleOwner = this
//        binding.viewModel = viewModel
//        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        composeView.setContent {

            DetailsScreen(viewModel = viewModel,args.post, edit = { editPost(args.post) })
            Log.d("Vip", "postClicked:${args.post.toString()} ")
        }
        // if(args.post.attachments.isNotEmpty()){bindMediaLayout(args.post.attachments)}


//        recyclerView = view.findViewById(R.id.recyclerView)
//        recyclerView.itemAnimator = null
//        replyAdapter = NestedReplyAdapter(
//            this@PostDetailsFragment,
//            0,
//            this@PostDetailsFragment,
//            this@PostDetailsFragment,
//            this@PostDetailsFragment
//        )
//        recyclerView.adapter = replyAdapter
//
//        lifecycleScope.launch {
//            viewModel.currentReplies.collectLatest {
//                Log.d("PostDetailsFragment", "onViewCreated: ${it.replies}")
//                replyAdapter.submitList(it.replies)
//
//            }
//        }

        //        replyEditText = view.findViewById(R.id.et_comment)
//        replyEditTextLayout = view.findViewById(R.id.tl_comment)
//        replyButton = view.findViewById(R.id.btn_send)
//        linearLayout = view.findViewById(R.id.linearLayout)
//        val post = view.findViewById<MaterialButton>(R.id.img_addComment)
//        post.setOnClickListener {
//            addComment(args.post)
//        }
//        binding.moreOptionPost.setOnClickListener {
//            onMoreOptionClicked(binding.moreOptionPost,args.post)
//        }
//        binding.imageViewUpvotePost.setOnClickListener {
//            viewModel.upVote(args.post)
//        }
//        binding.imageViewDownvotePost.setOnClickListener {
//            viewModel.downVote(args.post)
//        }
//    }
//
//    private fun bindMediaLayout(attachments: List<PostAttachment>) {
//        binding.mediaFramelayout.visibility = View.VISIBLE
//        val IMAGE_TYPES = listOf(
//            MimeType.JPEG.readableType,
//            MimeType.PNG.readableType,
//            MimeType.GIF.readableType,
//            MimeType.BMP.readableType,
//            MimeType.TIFF.readableType,
//            MimeType.WEBP.readableType,
//            MimeType.SVG.readableType
//        )
//
//        val VIDEO_TYPES = listOf(
//            MimeType.MP4.readableType,
//            MimeType.AVI.readableType,
//            MimeType.MKV.readableType,
//            MimeType.MOV.readableType,
//            MimeType.WMV.readableType
//        )
//        when(attachments.first().type){
//            in IMAGE_TYPES -> {
//                bindImageAttachments(attachments)
//            }
//            in VIDEO_TYPES ->{
//                bindVideoAttachments(attachments)
//            }
//            else -> {
//                bindFileAttachments(attachments)
//            }
//        }
//    }
//
//
//    @SuppressLint("ClickableViewAccessibility")
//    private fun bindImageAttachments(attachments: List<PostAttachment>) {
//        binding.mediaRecyclerview.visibility = View.GONE
//        binding.mediaViewpager.visibility = View.VISIBLE
//        binding.springDotsIndicator.visibility = View.VISIBLE
//        binding.mediaViewpager.adapter =  ImageAttachmentAdapter(attachments)
//        binding.mediaViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
//        binding.springDotsIndicator.attachTo(binding.mediaViewpager)
//    }
//
//    private fun bindVideoAttachments(attachments: List<PostAttachment>) {
//        //TODO: bind video attachments
//        bindFileAttachments(attachments)
//    }
//
//    private fun bindFileAttachments(attachments: List<PostAttachment>) {
//        binding.mediaRecyclerview.visibility = View.VISIBLE
//        binding.mediaViewpager.visibility = View.GONE
//        binding.springDotsIndicator.visibility = View.GONE
//        val adapter = FileAttachmentAdapter(object: OnFileClickedListener{
//            override fun onFileClicked(attachment: PostAttachment) {
//                Snackbar.make(
//                    binding.root,
//                    "File Clicked ${attachment.name}",
//                    Snackbar.LENGTH_SHORT).show()
//            }
//
//        }, requireContext())
//        adapter.submitList(attachments.toMutableList())
//        binding.mediaRecyclerview.adapter = adapter
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun addComment(post: Post) {
//        replyEditText.requestFocus()
//        linearLayout.visibility = View.VISIBLE
//        //open keyboard
//        val inputMethodManager =
//            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.showSoftInput(replyEditText, InputMethodManager.SHOW_IMPLICIT)
//        replyButton.setOnClickListener {
//            viewModel.insertReply(
//                Reply(
//                    postId = post.id,
//                    parentReplyId = null,
//                    depth = 0,
//                    content = replyEditText.text.toString(),
//                    createdAt =Date().toString(),
//                    authorEmail = currentUser?.email.toString()
//                )
//            )
//            replyEditText.setText("")
//            replyEditText.clearFocus()
//            linearLayout.visibility = View.GONE
//            inputMethodManager.hideSoftInputFromWindow(replyEditText.windowToken, 0)
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//     fun onAddReplyClicked(reply: NestedReplyItem) {
//        val currentReply = reply.reply
//        replyEditText.requestFocus()
//        linearLayout.visibility = View.VISIBLE
//        //open keyboard
//        val inputMethodManager =
//            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.showSoftInput(replyEditText, InputMethodManager.SHOW_IMPLICIT)
//        replyButton.setOnClickListener {
//            viewModel.insertReply(
//                Reply(
//                    postId = currentReply!!.postId,
//                    parentReplyId = currentReply.id,
//                    depth = currentReply.depth.plus(1),
//                    content = replyEditText.text.toString(),
//                    createdAt = Date().toString(),
//                    authorEmail = currentUser?.email.toString()
//                )
//            )
//            replyEditText.setText("")
//            replyEditText.clearFocus()
//            linearLayout.visibility = View.GONE
//            inputMethodManager.hideSoftInputFromWindow(replyEditText.windowToken, 0)
//        }
//    }
//
//     fun onUpVotePressed(comment: Reply) {
//        viewModel.replyUpVote(comment)
//    }
//
//     fun onDownVotePressed(comment: Reply) {
//        viewModel.replyDownVote(comment)
//    }
//
//
//     fun onMoreOptionClicked(imageView5: MaterialButton, postitem: Post) {
//        var resourceXml=R.menu.extra_option_menu
//        if(currentUser?.email!=postitem.authorEmail){
//            resourceXml=R.menu.extra_option_menu_not_owner
//        }
//        val popupMenu = PopupMenu(requireActivity(), imageView5)
//        popupMenu.menuInflater.inflate(resourceXml, popupMenu.menu)
//
//        // Set item click listener for the popup menu
//        popupMenu.setOnMenuItemClickListener { item ->
//            when (item.itemId) {
//                R.id.item_save -> {
//                    // Handle Item 1 click
//                    // Add your code here
//                    true
//                }
//                R.id.item_delete -> {
//                    viewModel.deletePost(postitem)
//                    true
//                }
//                R.id.item_report -> {
//                    true
//                }
//                R.id.item_edit -> {
//                    val action = PostDetailsFragmentDirections.actionPostDetialsFragmentToEditPostFragment(postitem)
//                    findNavController().navigate(action)
//                    true
//                }
//                else -> false
//            }
//        }
//        // Show the popup menu
//        popupMenu.show()
//    }
//
//     fun onMoreOptionClicked(imageView5: MaterialButton, reply: Reply) {
//        var resourceXml=R.menu.extra_option_menu
//        if(currentUser?.email!=reply.authorEmail){
//            resourceXml=R.menu.extra_option_menu_not_owner
//        }
//        val popupMenu = PopupMenu(requireActivity(), imageView5)
//        popupMenu.menuInflater.inflate(resourceXml, popupMenu.menu)
//
//        // Set item click listener for the popup menu
//        popupMenu.setOnMenuItemClickListener { item ->
//            when (item.itemId) {
//                R.id.item_delete -> {
//                    findNavController().navigate(R.id.action_postDetialsFragment_to_feedFragment)
//                    viewModel.deleteReply(reply)
//                    true
//                }
//                R.id.item_edit -> {
//                    replyEditText.requestFocus()
//                    linearLayout.visibility = View.VISIBLE
//                    //open keyboard
//                    val inputMethodManager =
//                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    inputMethodManager.showSoftInput(replyEditText, InputMethodManager.SHOW_IMPLICIT)
//                    replyEditText.setText(reply.content)
//                    replyButton.setOnClickListener {
//                        viewModel.updateReply(
//                            reply.copy(
//                                content = replyEditText.text.toString(),
//                                editStatus = true
//                            )
//                        )
//                        replyEditText.setText("")
//                        replyEditText.clearFocus()
//                        linearLayout.visibility = View.GONE
//                        inputMethodManager.hideSoftInputFromWindow(replyEditText.windowToken, 0)
//                    }
//
//                    true
//                }
//                R.id.item_report -> {
//                    //todo after ml model feature
//                    true
//                }
//                R.id.item_save -> {
//                    //todo after bookmark feature
//                    true
//                }
//                else -> false
//            }
//        }
//        popupMenu.show()
//    }
//
//     fun onReplyCollapsed(reply: Reply) {
//        if(reply.collapsed){
//            viewModel.collapsedReplies.remove(reply.id)
//        }else {
//            viewModel.collapsedReplies.add(reply.id)
//        }
//        Log.d("zarea in fragment",viewModel.collapsedReplies.toString())
//    }
//
//    fun onTagClick(tag: Tag) {
//        val action = PostDetailsFragmentDirections.actionPostDetailsFragmentToSearchFragment2(tag.label, true)
//        findNavController().navigate(action)
//    }


    }

        fun editPost(postItem: Post) {
            val action =
                PostDetailsFragmentDirections.actionPostDetialsFragmentToEditPostFragment(postItem)
            findNavController().navigate(action)
            true
        }}