package com.augieafr.storyapp.presentation.list_story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.augieafr.storyapp.data.model.ListStoryItem
import com.augieafr.storyapp.databinding.ItemStoryBinding

class ListStoryAdapter : ListAdapter<ListStoryItem, ListStoryViewHolder>(
    object : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem) =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem) =
            oldItem == newItem

    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListStoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}