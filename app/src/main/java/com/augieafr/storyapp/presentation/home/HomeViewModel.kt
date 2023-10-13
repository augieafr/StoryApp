package com.augieafr.storyapp.presentation.home

import androidx.lifecycle.ViewModel
import com.augieafr.storyapp.data.local.preferences.UserPreference

class HomeViewModel(private val userPreference: UserPreference) : ViewModel() {
    var userToken = userPreference.getUserToken()

    suspend fun logout() {
        userPreference.setUserToken("")
    }
}