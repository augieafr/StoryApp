package com.augieafr.storyapp.presentation.add_story

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.augieafr.storyapp.data.local.preferences.UserPreference
import com.augieafr.storyapp.data.model.ErrorResponse
import com.augieafr.storyapp.data.remote.ApiService
import com.augieafr.storyapp.data.utils.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryViewModel(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) : ViewModel() {

    var storyImageUri: Uri? = null
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isSuccessUpload: MutableLiveData<Boolean> = MutableLiveData()
    val isSuccessUpload: LiveData<Boolean> get() = _isSuccessUpload

    fun uploadStory(photoUri: Uri, description: String, context: Context) = viewModelScope.launch {
        _isLoading.postValue(true)
        withContext(Dispatchers.IO) {
            userPreference.getUserToken().take(1).collect { token ->
                val imageFile = uriToFile(photoUri, context)
                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )

                val result = apiService.createStory(
                    token = token,
                    photo = multipartBody,
                    description = requestBody
                )

                if (result.isSuccessful) {
                    _isLoading.postValue(false)
                    result.body()?.let {
                        if (!it.error) {
                            _isSuccessUpload.postValue(true)
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
    }
}