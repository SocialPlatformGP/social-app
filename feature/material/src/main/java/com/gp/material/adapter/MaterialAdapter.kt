package com.gp.material.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gp.material.R
import com.gp.material.listener.ClickOnFileClicKListener
import com.gp.material.model.MaterialItem

class MaterialAdapter(private val fileClickListener: ClickOnFileClicKListener) :
    ListAdapter<MaterialItem, MaterialAdapter.MaterialViewHolder>(MaterialDiffUtil()) {

    inner class MaterialViewHolder(private val binding: ViewDataBinding?) :
        RecyclerView.ViewHolder(binding!!.root) {
        fun bind(file: MaterialItem) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val binding: ViewDataBinding? = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.material_item,
            parent,
            false
        )
        return MaterialViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val file = getItem(position)
        holder.bind(file)

        holder.itemView.setOnClickListener {
        }
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
