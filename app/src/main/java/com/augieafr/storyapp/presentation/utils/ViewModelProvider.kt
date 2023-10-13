package com.augieafr.storyapp.presentation.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.augieafr.storyapp.data.local.preferences.UserPreference
import com.augieafr.storyapp.data.local.preferences.dataStore
import com.augieafr.storyapp.data.remote.ApiConfig
import com.augieafr.storyapp.presentation.add_story.AddStoryViewModel
import com.augieafr.storyapp.presentation.auth.AuthViewModel
import com.augieafr.storyapp.presentation.home.HomeViewModel
import com.augieafr.storyapp.presentation.list_story.ListStoryViewModel

class ViewModelProvider(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(getUserPreference(), getApiConfig()) as T
        } else if (modelClass.isAssignableFrom(ListStoryViewModel::class.java)) {
            return ListStoryViewModel(getUserPreference(), getApiConfig()) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(getUserPreference(), getApiConfig()) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(getUserPreference()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    private fun getUserPreference() = UserPreference.getInstance(context.dataStore)
    private fun getApiConfig() = ApiConfig.getApiService()
}