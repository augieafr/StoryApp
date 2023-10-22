package com.augieafr.storyapp.data.utils

import com.augieafr.storyapp.data.model.entity.StoryEntity
import com.augieafr.storyapp.data.model.response.ErrorResponse
import com.augieafr.storyapp.data.model.response.ListStoryItem
import com.augieafr.storyapp.presentation.model.StoryUIModel
import com.google.gson.Gson
import retrofit2.Response

fun ListStoryItem.toStoryEntity() = StoryEntity(
    id = id,
    name = name,
    description = description,
    photoUrl = photoUrl,
    lat = lat,
    lon = lon,
)

fun ListStoryItem.toStoryUIModel() = StoryUIModel(
    id = id,
    userName = name,
    description = description,
    photoUrl = photoUrl,
    lat = lat?.toDouble(),
    lon = lon?.toDouble(),
)

fun StoryEntity.toStoryUIModel() = StoryUIModel(
    id = id,
    userName = name,
    description = description,
    photoUrl = photoUrl,
    lat = lat?.toDouble(),
    lon = lon?.toDouble(),
)

fun <T> Response<T>.getException(): Exception {
    val errorResponse = Gson().fromJson(errorBody()?.string(), ErrorResponse::class.java)
    return Exception(errorResponse.message)
}