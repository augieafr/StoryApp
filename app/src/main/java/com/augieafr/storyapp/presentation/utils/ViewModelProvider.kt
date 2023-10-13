package com.augieafr.storyapp.presentation.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.augieafr.storyapp.data.local.preferences.UserPreference
import com.augieafr.storyapp.data.remote.ApiService
import com.augieafr.storyapp.presentation.add_story.AddStoryViewModel
import com.augieafr.storyapp.presentation.auth.AuthViewModel
import com.augieafr.storyapp.presentation.list_story.ListStoryViewModel

class ViewModelProvider(private val pref: UserPreference, private val apiService: ApiService) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(pref, apiService) as T
        } else if (modelClass.isAssignableFrom(ListStoryViewModel::class.java)) {
            return ListStoryViewModel(pref, apiService) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(pref, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}