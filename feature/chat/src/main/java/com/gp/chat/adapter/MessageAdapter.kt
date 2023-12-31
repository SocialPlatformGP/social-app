 package com.gp.chat.adapter

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.gp.chat.R
import com.gp.chat.databinding.AudioPlayerBinding
import com.gp.chat.databinding.FileMessageBinding
import com.gp.chat.databinding.ImageMessageBinding
import com.gp.chat.databinding.MessageBinding
import com.gp.chat.listener.ImageClickListener
import com.gp.chat.listener.OnFileClickListener
import com.gp.chat.listener.OnMessageClickListener
import com.gp.chat.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MessageAdapter(
    private val messageClickListener: OnMessageClickListener,
    private val fileClickListener: OnFileClickListener,
    private val imageClickListener: ImageClickListener,
    private val isPrivateChats: Boolean
) :
    ListAdapter<Message,ViewHolder>(MessageDiffCallback()) {
    private val currentUser = Firebase.auth.currentUser
    private var mediaPlayer: MediaPlayer? = null
    private var progressBarJob: Job? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_TEXT) {
            val view = inflater.inflate(R.layout.message, parent, false)
            val binding = MessageBinding.bind(view)
            MessageViewHolder(binding)
        } else if (viewType == VIEW_TYPE_IMAGE) {
            val view = inflater.inflate(R.layout.image_message, parent, false)
            val binding = ImageMessageBinding.bind(view)
            ImageMessageViewHolder(binding)
        } else if (viewType == VIEW_TYPE_PDF) {
            val view = inflater.inflate(R.layout.file_message, parent, false)
            val binding = FileMessageBinding.bind(view)
            FileMessageViewHolder(binding)
        } else if (viewType == VIEW_TYPE_AUDIO) {
            val view = inflater.inflate(R.layout.audio_player, parent, false)
            val binding = AudioPlayerBinding.bind(view)
            AudioMessageViewHolder(binding)
        } else {
            val view = inflater.inflate(R.layout.message, parent, false)
            val binding = MessageBinding.bind(view)
            MessageViewHolder(binding)
        }


    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnLongClickListener {
            if (item.senderName == currentUser?.displayName) {
                createPopUpMenu(holder.itemView, item)
            }
            true
        }

        when (holder) {
            is MessageViewHolder -> {
                holder.bind(item)
            }

            is ImageMessageViewHolder -> {
                holder.bind(item)
            }

            is FileMessageViewHolder -> {
                holder.bind(item)
            }

            is AudioMessageViewHolder -> {
                holder.bind(getItem(position))
            }
        }

    }

    private fun createPopUpMenu(item: View, message: Message) {
        val messageId = message.id
        val popupMenu = PopupMenu(item.context, item)
        popupMenu.menuInflater.inflate(R.menu.message_option, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_update -> {
                    messageClickListener.updateMessage(messageId, message.groupId, message.message)
                }

                R.id.menu_item_delete -> {
                    messageClickListener.deleteMessage(messageId, message.groupId)
                }
            }
            true
        }
        popupMenu.show()
    }


    override fun getItemViewType(position: Int) =
        with(getItem(position).fileType) {
            if (contains("text")) {
                VIEW_TYPE_TEXT
            } else if (contains("image")) {
                VIEW_TYPE_IMAGE
            } else if (contains("application/pdf")) {
                VIEW_TYPE_PDF
            } else if (contains("audio")) {
                VIEW_TYPE_AUDIO
            } else {
                VIEW_TYPE_PDF
            }
        }




    inner class MessageViewHolder(private val binding: MessageBinding) : ViewHolder(binding.root) {
        fun bind(item: Message) {
            //data
            //sender
            if (item.senderName != currentUser?.displayName) {
                binding.receieverTextMessageLayout.visibility = View.VISIBLE
                binding.senderTextMessageLayout.visibility = View.GONE
                if (item.message.isNotEmpty()) {
                    binding.receivedMessageTextview.visibility = View.VISIBLE
                    binding.receivedMessageTextview.text = item.message
                } else {
                    binding.receivedMessageTextview.visibility = View.GONE
                }

                if (!isPrivateChats) {
                    loadImageIntoView(binding.receivedMessageUserpfp, item.senderPfpURL)
                    binding.receivedMessageUsernameTextview.text = item.senderName
                } else {
                    binding.receivedMessageUserpfp.visibility = View.GONE
                    binding.receivedMessageUsernameTextview.visibility = View.GONE
                }
                binding.receivedMessageTimestamp.text = item.timestamp

            } else {
                binding.senderTextMessageLayout.visibility = View.VISIBLE
                binding.receieverTextMessageLayout.visibility = View.GONE
                if (item.message.isNotEmpty()) {
                    binding.sentMessageTextview.visibility = View.VISIBLE
                    binding.sentMessageTextview.text = item.message
                } else {
                    binding.sentMessageTextview.visibility = View.GONE
                }
                binding.sentMessageTimestamp.text = item.timestamp
            }
        }


    }

    inner class ImageMessageViewHolder(private val binding: ImageMessageBinding) :
        ViewHolder(binding.root) {
        fun bind(item: Message) {
            Log.d("MessageAdapter02", "bind: ${item.message}")

            if (item.senderName != currentUser?.displayName) {
                binding.recieverImageMessageLayout.visibility = View.VISIBLE
                binding.senderImageMessageLayout.visibility = View.GONE
                loadImageIntoView(binding.receivedMessageImageview, item.fileURI.toString(), false)

                if (!isPrivateChats) {
                    loadImageIntoView(binding.receivedMessageUserpfp, item.senderPfpURL)
                    binding.receivedMessageUsernameTextview.text = item.senderName ?: ""
                } else {
                    binding.receivedMessageUserpfp.visibility = View.GONE
                    binding.receivedMessageUsernameTextview.visibility = View.GONE
                }
                if (item.message.isNotEmpty()) {
                    binding.receivedMessageTextview.visibility = View.VISIBLE
                    binding.receivedMessageTextview.text = item.message
                } else {
                    binding.receivedMessageTextview.visibility = View.GONE
                }
                binding.receivedMessageTimestamp.text = item.timestamp


            } else {
                binding.senderImageMessageLayout.visibility = View.VISIBLE
                binding.recieverImageMessageLayout.visibility = View.GONE
                loadImageIntoView(binding.sentMessageImageview, item.fileURI.toString(), false)
                if (item.message.isNotEmpty()) {
                    binding.sentMessageTextview.visibility = View.VISIBLE
                    binding.sentMessageTextview.text = item.message
                } else {
                    binding.sentMessageTextview.visibility = View.GONE
                }
                binding.sentMessageTimestamp.text = item.timestamp
            }

            //click listener
            binding.imageMessageLayout.setOnClickListener {
                imageClickListener.onImageClick(item.fileURI.toString())
            }
        }
    }

    inner class FileMessageViewHolder(private val binding: FileMessageBinding) :
        ViewHolder(binding.root) {
        fun bind(item: Message) {
            //data
            if (item.senderName != currentUser?.displayName) {
                binding.recieverFileMessageLayout.visibility = View.VISIBLE
                binding.senderFileMessageLayout.visibility = View.GONE
                binding.receivedMessageImageview.setImageResource(R.drawable.file_icon)

                if (!isPrivateChats) {
                    loadImageIntoView(binding.receivedMessageUserpfp, item.senderPfpURL)
                    binding.receivedMessageUsernameTextview.text = item.senderName ?: ""
                } else {
                    binding.receivedMessageUserpfp.visibility = View.GONE
                    binding.receivedMessageUsernameTextview.visibility = View.GONE
                }
                if (item.message.isNotEmpty()) {
                    binding.receivedMessageTextview.visibility = View.VISIBLE
                    binding.receivedMessageTextview.text = item.message
                } else {
                    binding.receivedMessageTextview.visibility = View.GONE
                }
                binding.receivedMessageTimestamp.text = item.timestamp
                binding.receivedMessageFileName.text = item.fileNames

            } else {
                binding.senderFileMessageLayout.visibility = View.VISIBLE
                binding.recieverFileMessageLayout.visibility = View.GONE
                binding.sentMessageImageview.setImageResource(R.drawable.file_icon)

                if (item.message.isNotEmpty()) {
                    binding.sentMessageTextview.visibility = View.VISIBLE
                    binding.sentMessageTextview.text = item.message
                } else {
                    binding.sentMessageTextview.visibility = View.GONE
                }
                binding.sentMessageTimestamp.text = item.timestamp
                binding.sentMessageFileName.text = item.fileNames
            }
            //click listener
            binding.fileMessageLayout.setOnClickListener {
                if(item.fileURI.toString().startsWith("content://")){
                    Toast.makeText(itemView.context, "Uploading not finished", Toast.LENGTH_SHORT).show()
                }
                else {
                    fileClickListener.onFileClick(
                        item.fileURI.toString(),
                        item.fileType,
                        item.fileNames
                    )
                }
            }
        }
    }

    inner class AudioMessageViewHolder(private val binding: AudioPlayerBinding) :
        ViewHolder(binding.root) {

        private var isRunning = false
        private var audioDuration = 0
        private var currentPosition = 0


        fun bind(item: Message) {
            //sender
            binding.messengerTextView.text = item.senderName ?: ""
            if (item.senderName == currentUser?.displayName) {
                loadImageIntoView(binding.messengerImageView, currentUser.photoUrl.toString())
            } else {
                loadImageIntoView(binding.messengerImageView, item.senderPfpURL)
            }
            //data
            val audioUrl = item.fileURI

            binding.playStopButton.setOnClickListener {
                if (isRunning) {
                    pauseAudio()
                } else {
                    if (currentPosition == 0) {
                        playAudio(audioUrl)
                    } else {
                        resumeAudio()
                    }
                }
            }
        }

        private fun resumeAudio() {
            mediaPlayer?.let {
                it.seekTo(currentPosition)
                it.start()
                isRunning = true
                updateSeekBar()
                binding.playStopButton.setImageResource(R.drawable.baseline_pause_24)
            }
        }

        private fun pauseAudio() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    currentPosition = it.currentPosition
                    isRunning = false
                    progressBarJob?.cancel()
                    binding.playStopButton.setImageResource(R.drawable.baseline_play_arrow_24)
                }
            }
        }

        private fun playAudio(audioUrl: Uri) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(binding.root.context, audioUrl)
                prepareAsync()
                setOnPreparedListener {
                    audioDuration = it.duration
                    binding.audioSeekBar.max = audioDuration
                    it.start()
                    isRunning = true
                    updateSeekBar()
                    binding.playStopButton.setImageResource(R.drawable.baseline_pause_24)
                }
                setOnCompletionListener {
                    stopAudio()

                }
            }
        }

        private fun stopAudio() {
            mediaPlayer?.let {
                if (isRunning) {
                    it.stop()
                    it.release()
                    mediaPlayer = null
                    isRunning = false
                    binding.audioSeekBar.progress = 0
                    binding.playStopButton.setImageResource(R.drawable.baseline_play_arrow_24)
                    progressBarJob?.cancel()
                }
            }
        }

        private fun updateSeekBar() {
            progressBarJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    mediaPlayer?.let { player ->
                        if (player.isPlaying) {
                            val currentPosition = player.currentPosition
                            binding.audioSeekBar.progress = currentPosition
                            delay(1000)
                        }
                    }
                }
            }
        }

    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        mediaPlayer?.release()
        mediaPlayer?.seekTo(0)
        mediaPlayer = null
        progressBarJob?.cancel()


    }


    private fun loadImageIntoView(view: ImageView, url: String, isCircular: Boolean = true) {
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    loadWithGlide(view, downloadUrl, isCircular)
                }
                .addOnFailureListener { e ->
                    Log.w(
                        TAG,
                        "Getting download url was not successful.",
                        e
                    )
                }
        } else {
            loadWithGlide(view, url, isCircular)
        }
    }

    private fun loadWithGlide(view: ImageView, url: String, isCircular: Boolean = true) {
        Log.d(TAG, "loadWithGlide: $url")
        Glide.with(view.context).load(url).into(view)
        var requestBuilder = Glide.with(view.context).load(url)
        if (isCircular) {
            requestBuilder = requestBuilder.transform(CircleCrop())
        }
        requestBuilder.into(view)
    }

    companion object {
        const val TAG = "MessageAdapter"
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
        const val VIEW_TYPE_PDF = 3
        const val VIEW_TYPE_AUDIO = 4
    }
}
class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return false
    }
}





