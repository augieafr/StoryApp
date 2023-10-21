package com.augieafr.storyapp.data.model.response

import com.google.gson.annotations.SerializedName

data class ListStoryResponse(

    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

data class ListStoryItem(

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("lon")
    val lon: Float? = null,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("lat")
    val lat: Float? = null
)
