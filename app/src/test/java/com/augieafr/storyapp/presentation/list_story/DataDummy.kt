package com.augieafr.storyapp.presentation.list_story

import com.augieafr.storyapp.presentation.model.StoryUIModel

object DataDummy {
    fun generateDummyListStoryUIModel(): List<StoryUIModel> {
        val list = mutableListOf<StoryUIModel>()
        for (i in 0..100) {
            list.add(
                StoryUIModel(
                    id = "id$i",
                    userName = "user$i",
                    photoUrl = "photoUrl$i",
                    description = "description$i",
                    lat = 0.0,
                    lon = 0.0
                )
            )
        }

        return list
    }
}