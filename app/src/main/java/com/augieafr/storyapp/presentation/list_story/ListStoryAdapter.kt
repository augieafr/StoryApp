package com.augieafr.storyapp.presentation.list_story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.augieafr.storyapp.databinding.ItemStoryBinding
import com.augieafr.storyapp.presentation.list_story.model.StoryUIModel

class ListStoryAdapter : ListAdapter<StoryUIModel, ListStoryViewHolder>(
    object : DiffUtil.ItemCallback<StoryUIModel>() {
        override fun areItemsTheSame(oldItem: StoryUIModel, newItem: StoryUIModel) =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: StoryUIModel, newItem: StoryUIModel) =
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