package com.augieafr.storyapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.augieafr.storyapp.data.local.preferences.UserPreference
import com.augieafr.storyapp.data.model.response.ListStoryItem
import com.augieafr.storyapp.data.paging.StoryPagingSource
import com.augieafr.storyapp.data.remote.ApiService
import com.augieafr.storyapp.data.utils.RepositoryWithToken
import com.augieafr.storyapp.data.utils.ResultState
import com.augieafr.storyapp.data.utils.toErrorResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
    userPreference: UserPreference
) : RepositoryWithToken(userPreference) {

    suspend fun getPagingStories(): Flow<PagingData<ListStoryItem>> {
        val token = super.getUserToken()
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).flow
    }

    fun getDetailStories(id: String) = executeRequest { flowCollector, token ->
        val result = apiService.getDetailStory(token, id)
        flowCollector.emit(ResultState.Loading(false))
        if (result.isSuccessful) {
            result.body()?.story?.let {
                flowCollector.emit(ResultState.Success(it))
            } ?: run {
                flowCollector.emit(ResultState.Error(""))
            }
        } else {
            flowCollector.emit(ResultState.Error(result.toErrorResponse().message))
        }
    }

    fun addStory(imageFile: File, description: String) = executeRequest { flowCollector, token ->
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val result = apiService.createStory(token, multipartBody, requestBody)
        if (result.isSuccessful) {
            flowCollector.emit(ResultState.Success(true))
        } else {
            flowCollector.emit(ResultState.Error(result.toErrorResponse().message))
        }
    }
}