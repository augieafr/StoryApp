package com.augieafr.storyapp.presentation.model

data class StoryUIModel(
    val id: String,
    val userName: String,
    val description: String,
    val photoUrl: String,
    val lat: Float? = null,
    val lon: Float? = null
)