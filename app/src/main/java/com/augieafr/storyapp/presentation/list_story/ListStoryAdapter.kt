package com.augieafr.storyapp.presentation.list_story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.augieafr.storyapp.data.model.response.ListStoryItem
import com.augieafr.storyapp.databinding.ItemStoryBinding

class ListStoryAdapter(private val onItemClickListener: (String) -> Unit) :
    PagingDataAdapter<ListStoryItem, ListStoryViewHolder>(
        object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem) =
                oldItem.id == newItem.id


            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem) =
                oldItem == newItem

        }
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoryViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ListStoryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}