package com.augieafr.storyapp.presentation.list_story

import androidx.recyclerview.widget.RecyclerView
import com.augieafr.storyapp.data.model.ListStoryItem
import com.augieafr.storyapp.databinding.ItemStoryBinding
import com.bumptech.glide.Glide

class ListStoryViewHolder(private val binding: ItemStoryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(story: ListStoryItem) = with(binding) {
        tvDescription.text = story.description
        tvName.text = story.name

        Glide.with(binding.root)
            .load(story.photoUrl)
            .into(imgName)
    }
}