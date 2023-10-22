package com.augieafr.storyapp.presentation.list_story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.augieafr.storyapp.data.repository.StoryRepository

class ListStoryViewModel(
    private val repository: StoryRepository
) : ViewModel() {
    suspend fun getAllStory() =
        repository.getPagingStories().cachedIn(viewModelScope)
}