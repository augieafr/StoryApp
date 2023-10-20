package com.augieafr.storyapp.presentation.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.augieafr.storyapp.data.di.Injection
import com.augieafr.storyapp.presentation.add_story.AddStoryViewModel
import com.augieafr.storyapp.presentation.auth.AuthViewModel
import com.augieafr.storyapp.presentation.detail_story.DetailStoryViewModel
import com.augieafr.storyapp.presentation.home.HomeViewModel
import com.augieafr.storyapp.presentation.list_story.ListStoryViewModel

class ViewModelProvider(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(Injection.provideAuthRepository(context)) as T
        } else if (modelClass.isAssignableFrom(ListStoryViewModel::class.java)) {
            return ListStoryViewModel(Injection.provideStoryRepository(context)) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(Injection.provideStoryRepository(context)) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(Injection.provideUserPreference(context)) as T
        } else if (modelClass.isAssignableFrom(DetailStoryViewModel::class.java)) {
            return DetailStoryViewModel(Injection.provideStoryRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}