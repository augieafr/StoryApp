package com.augieafr.storyapp.presentation.nearby_story

import androidx.lifecycle.ViewModel
import com.augieafr.storyapp.data.repository.StoryRepository

class NearbyStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    suspend fun getNearbyStory() = storyRepository.getStories(1)

}