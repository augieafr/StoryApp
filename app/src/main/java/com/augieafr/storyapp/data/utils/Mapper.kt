package com.augieafr.storyapp.data.utils

import com.augieafr.storyapp.data.model.entity.StoryEntity
import com.augieafr.storyapp.data.model.response.ListStoryItem
import com.augieafr.storyapp.presentation.model.StoryUIModel

fun ListStoryItem.toStoryEntity() = StoryEntity(
    id = id,
    name = name,
    description = description,
    photoUrl = photoUrl,
    lat = lat,
    lon = lon,
)

fun StoryEntity.toStoryUIModel() = StoryUIModel(
    id = id,
    userName = name,
    description = description,
    photoUrl = photoUrl,
    lat = lat,
    lon = lon,
)