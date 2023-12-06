package com.gp.material.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gp.material.R
import com.gp.material.databinding.MaterialItemBinding
import com.gp.material.listener.ClickOnFileClicKListener
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
                fileClickListener.openFile(item)
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
                    fileClickListener.showDetails(file)

                    true
                }
                else -> false
            }
        }

        popupMenu.show()
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
