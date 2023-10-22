package com.augieafr.storyapp.presentation.list_story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.augieafr.storyapp.databinding.ItemStoryBinding
import com.augieafr.storyapp.presentation.model.StoryUIModel

class ListStoryAdapter(private val onItemClickListener: (String) -> Unit) :
    PagingDataAdapter<StoryUIModel, ListStoryViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoryViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ListStoryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryUIModel>() {
            override fun areItemsTheSame(oldItem: StoryUIModel, newItem: StoryUIModel) =
                oldItem.id == newItem.id


            override fun areContentsTheSame(oldItem: StoryUIModel, newItem: StoryUIModel) =
                oldItem == newItem

        }
    }
}