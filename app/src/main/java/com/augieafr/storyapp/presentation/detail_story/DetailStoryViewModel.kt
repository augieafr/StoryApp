package com.augieafr.storyapp.presentation.detail_story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.augieafr.storyapp.data.local.preferences.UserPreference
import com.augieafr.storyapp.data.model.ErrorResponse
import com.augieafr.storyapp.data.model.Story
import com.augieafr.storyapp.data.remote.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailStoryViewModel(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> get() = _story

    fun getDetailStory(id: String) = viewModelScope.launch {
        _isLoading.value = true
        withContext(Dispatchers.IO) {
            userPreference.getUserToken().take(1).collect {
                val result = apiService.getDetailStory(it, id)
                _isLoading.postValue(false)
                if (result.isSuccessful) {
                    result.body()?.let { detailResponse ->
                        if (detailResponse.error) {
                            _errorMessage.postValue(detailResponse.message)
                        } else {
                            _story.postValue(detailResponse.story)
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
    }
}