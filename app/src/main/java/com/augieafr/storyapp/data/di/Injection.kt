package com.augieafr.storyapp.data.di

import android.content.Context
import com.augieafr.storyapp.data.local.preferences.UserPreference
import com.augieafr.storyapp.data.local.preferences.dataStore
import com.augieafr.storyapp.data.remote.ApiConfig
import com.augieafr.storyapp.data.repository.AuthRepository
import com.augieafr.storyapp.data.repository.StoryRepository

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val preference = provideUserPreference(context)
        return StoryRepository(apiService, preference)
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        val preference = provideUserPreference(context)
        return AuthRepository(apiService, preference)
    }

    fun provideUserPreference(context: Context) = UserPreference.getInstance(context.dataStore)
}