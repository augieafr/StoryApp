package com.augieafr.storyapp.presentation.list_story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.augieafr.storyapp.data.local.preferences.UserPreference
import com.augieafr.storyapp.data.model.ErrorResponse
import com.augieafr.storyapp.data.model.ListStoryItem
import com.augieafr.storyapp.data.remote.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListStoryViewModel(
    private val preference: UserPreference,
    private val apiService: ApiService
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> get() = _listStory

    fun getAllStory() = viewModelScope.launch {
        _isLoading.value = true

        withContext(Dispatchers.IO) {
            preference.getUserToken().take(1).collect {
                getStoriesFromApi(it)
            }
        }
    }

    private suspend fun getStoriesFromApi(token: String) {
        val result = apiService.getStories(token)
        if (result.isSuccessful) {
            _isLoading.postValue(false)
            if (result.body()?.listStory?.isEmpty() == true) {
                _errorMessage.postValue(ListStoryFragment.NO_STORY_FOUND)
                return
            }
            result.body()?.let {
                if (!it.error) {
                    _listStory.postValue(it.listStory.orEmpty())
                } else {
                    _errorMessage.postValue(it.message)
                }
            }
        } else {
            val errorResponse =
                Gson().fromJson(result.errorBody()?.string(), ErrorResponse::class.java)
            _errorMessage.postValue(errorResponse.message)
            _isLoading.postValue(false)
        }
    }
}