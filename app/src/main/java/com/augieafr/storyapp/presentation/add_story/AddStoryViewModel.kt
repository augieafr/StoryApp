package com.augieafr.storyapp.presentation.add_story

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.augieafr.storyapp.data.repository.StoryRepository
import java.io.File

class AddStoryViewModel(
    private val repository: StoryRepository
) : ViewModel() {

    var storyImageUri: Uri? = null
    fun uploadStory(imageFile: File, description: String) =
        repository.addStory(imageFile, description)
}