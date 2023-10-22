package com.augieafr.storyapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.augieafr.storyapp.data.exceptions.NoDataException
import com.augieafr.storyapp.data.local.preferences.UserPreference
import com.augieafr.storyapp.data.local.room.StoryDatabase
import com.augieafr.storyapp.data.paging.StoryRemoteMediator
import com.augieafr.storyapp.data.remote.ApiService
import com.augieafr.storyapp.data.utils.RepositoryWithToken
import com.augieafr.storyapp.data.utils.ResultState
import com.augieafr.storyapp.data.utils.getException
import com.augieafr.storyapp.data.utils.toStoryUIModel
import com.augieafr.storyapp.presentation.model.StoryUIModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
    private val database: StoryDatabase,
    userPreference: UserPreference
) : RepositoryWithToken(userPreference) {

    @OptIn(ExperimentalPagingApi::class)
    suspend fun getPagingStories(): Flow<PagingData<StoryUIModel>> {
        val token = getUserToken()
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(apiService, database, token),
            pagingSourceFactory = {
                database.storyDao().getStories()
            }
        ).flow.map {
            it.map { storyEntity ->
                storyEntity.toStoryUIModel()
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getStories(location: Int = 0) = executeRequest { flowCollector, token ->
        val result = apiService.getStories(token, null, null, location)
        flowCollector.emit(ResultState.Loading(false))
        if (result.isSuccessful) {
            result.body()?.listStory?.let { listItem ->
                flowCollector.emit(ResultState.Success(listItem.map {
                    it.toStoryUIModel()
                }))
            } ?: run {
                flowCollector.emit(ResultState.Error(NoDataException()))
            }
        } else {
            flowCollector.emit(ResultState.Error(result.getException()))
        }
    }

    fun getDetailStories(id: String) = executeRequest { flowCollector, token ->
        val result = apiService.getDetailStory(token, id)
        flowCollector.emit(ResultState.Loading(false))
        if (result.isSuccessful) {
            result.body()?.story?.let {
                flowCollector.emit(ResultState.Success(it))
            } ?: run {
                flowCollector.emit(ResultState.Error(NoDataException()))
            }
        } else {
            flowCollector.emit(ResultState.Error(result.getException()))
        }
    }

    fun addStory(imageFile: File, description: String, lat: Double?, lon: Double?) =
        executeRequest { flowCollector, token ->
            val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartFile = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            val result = apiService.createStory(
                token,
                multipartFile,
                descriptionRequestBody,
                lat?.toFloat(),
                lon?.toFloat()
            )
            if (result.isSuccessful) {
                flowCollector.emit(ResultState.Success(true))
            } else {
                flowCollector.emit(ResultState.Error(result.getException()))
            }
        }
}