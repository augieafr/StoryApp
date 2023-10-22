package com.augieafr.storyapp.presentation.add_story

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.augieafr.storyapp.data.repository.StoryRepository
import java.io.File

class AddStoryViewModel(
    private val repository: StoryRepository
) : ViewModel() {

    private var userLatitude: Double? = null
    private var userLongitude: Double? = null

    var isShareLocationChecked = MutableLiveData(false)
    var storyImageUri: Uri? = null

    fun setUserLocation(latitude: Double, longitude: Double) {
        userLatitude = latitude
        userLongitude = longitude
    }

    fun clearUserLocation() {
        userLatitude = null
        userLongitude = null
    }

    fun uploadStory(imageFile: File, description: String) =
        repository.addStory(imageFile, description, userLatitude, userLongitude)
}