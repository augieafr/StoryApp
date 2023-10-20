package com.augieafr.storyapp.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.augieafr.storyapp.data.local.preferences.UserPreference

class HomeViewModel(private val userPreference: UserPreference) : ViewModel() {
    val userToken = userPreference.getUserToken()
    private val _isFabExpanded = MutableLiveData(false)
    val isFabExpanded: LiveData<Boolean> = _isFabExpanded

    suspend fun logout() {
        userPreference.setUserToken("")
    }

    fun changeFabState() {
        _isFabExpanded.value = !(isFabExpanded.value ?: false)
    }
}