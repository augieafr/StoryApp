package com.augieafr.storyapp.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.augieafr.storyapp.data.local.room.StoryDatabase
import com.augieafr.storyapp.data.model.entity.RemoteKeys
import com.augieafr.storyapp.data.model.entity.StoryEntity
import com.augieafr.storyapp.data.remote.ApiService
import com.augieafr.storyapp.data.utils.toErrorResponse
import com.augieafr.storyapp.data.utils.toStoryEntity

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
    private val token: String
) : RemoteMediator<Int, StoryEntity>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return try {
            val responseData = apiService.getStories(token, page, state.config.pageSize)
            if (responseData.isSuccessful) {
                val data = responseData.body()?.listStory.orEmpty()
                val endOfPaginationReached = data.isEmpty()
                storyDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        storyDatabase.remoteKeysDao().deleteRemoteKeys()
                        storyDatabase.storyDao().deleteStories()
                    }
                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = data.map {
                        RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                    }
                    storyDatabase.remoteKeysDao().insertAll(keys)
                    storyDatabase.storyDao().insertStories(data.map { it.toStoryEntity() })
                }
                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } else MediatorResult.Error(Exception(responseData.toErrorResponse().message))
        } catch (exception: Exception) {
            Log.e(this::class.simpleName, exception.message.toString())
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDatabase.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

}