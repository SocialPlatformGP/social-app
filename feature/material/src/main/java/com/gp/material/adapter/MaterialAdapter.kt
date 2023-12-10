package com.gp.material.adapter

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gp.material.R
import com.gp.material.databinding.MaterialItemBinding
import com.gp.material.listener.ClickOnFileClicKListener
import com.gp.material.model.FileType
import com.gp.material.model.MaterialItem

class MaterialAdapter(private val fileClickListener: ClickOnFileClicKListener) :
    ListAdapter<MaterialItem, MaterialAdapter.MaterialViewHolder>(MaterialDiffUtil()) {

    inner class MaterialViewHolder(private val binding: MaterialItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MaterialItem) {
            // Bind the item to the layout using data binding
            binding.material = item
            itemView.setOnLongClickListener {
                    createPopUpMenu(itemView,item)
                true
            }

            // Set the click listener
            binding.root.setOnClickListener {
                if(item.fileType==FileType.FOLDER){
                    fileClickListener.openFolder(item.path)
                } else{
                    fileClickListener.openFile(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MaterialItemBinding.inflate(layoutInflater, parent, false)
        return MaterialViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val file = getItem(position)
        holder.bind(file)
    }
    fun createPopUpMenu(item: View, file: MaterialItem) {
        val popupMenu = PopupMenu(item.context, item)
        popupMenu.menuInflater.inflate(R.menu.file_options, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.deleteFile -> {
                   fileClickListener.deleteFile(file)
                    true
                }
                R.id.downloadFile -> {
                    fileClickListener.downloadFile(file)
                    true
                }
                R.id.fileDetails -> {
                   showDetailsDialog(item.context,file)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showDetailsDialog(context: Context, item: MaterialItem) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Details")

        val message = """
        Path: ${item.path}
        FileType: ${item.fileType}
        Name: ${item.name}
        Created By: ${item.createdBy}
        File URL: ${item.fileUrl}
        Creation Time: ${item.creationTime}
    """.trimIndent()

        alertDialogBuilder.setMessage(message)

        alertDialogBuilder.setPositiveButton("Open Link") { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.fileUrl))
            context.startActivity(intent)
        }

        alertDialogBuilder.setNegativeButton("Share Link") { _, _ ->
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, item.fileUrl)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, null))
        }

        alertDialogBuilder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }


    private class MaterialDiffUtil : DiffUtil.ItemCallback<MaterialItem>() {
        override fun areItemsTheSame(oldItem: MaterialItem, newItem: MaterialItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MaterialItem, newItem: MaterialItem): Boolean {
            return oldItem == newItem
        }
    }
}
