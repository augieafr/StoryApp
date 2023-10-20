package com.augieafr.storyapp.presentation.detail_story

import androidx.lifecycle.ViewModel
import com.augieafr.storyapp.data.repository.StoryRepository

class DetailStoryViewModel(
    private val repository: StoryRepository,
) : ViewModel() {
    fun getDetailStory(id: String) = repository.getDetailStories(id)
}