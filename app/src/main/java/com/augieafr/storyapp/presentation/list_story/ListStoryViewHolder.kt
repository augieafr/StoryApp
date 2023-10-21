package com.augieafr.storyapp.presentation.list_story

import androidx.recyclerview.widget.RecyclerView
import com.augieafr.storyapp.databinding.ItemStoryBinding
import com.augieafr.storyapp.presentation.model.StoryUIModel
import com.bumptech.glide.Glide

class ListStoryViewHolder(
    private val binding: ItemStoryBinding,
    private val onItemClickListener: (String) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(story: StoryUIModel) = with(binding) {
        tvDescription.text = story.description
        tvName.text = story.userName

        Glide.with(binding.root)
            .load(story.photoUrl)
            .into(imgName)

        root.setOnClickListener {
            onItemClickListener(story.id)
        }
    }
}