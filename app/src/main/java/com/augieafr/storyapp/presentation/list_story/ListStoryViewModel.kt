package com.augieafr.storyapp.presentation.list_story

import androidx.lifecycle.ViewModel
import com.augieafr.storyapp.data.repository.StoryRepository

class ListStoryViewModel(
    private val repository: StoryRepository
) : ViewModel() {
    fun getAllStory() =
        repository.getStories()
}