package com.augieafr.storyapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.augieafr.storyapp.data.model.ListStoryItem
import com.augieafr.storyapp.data.remote.ApiService
import com.augieafr.storyapp.data.utils.toErrorResponse

class StoryPagingSource(private val apiService: ApiService, private val token: String) :
    PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val result = apiService.getStories(token, position, params.loadSize)
            if (result.isSuccessful) {
                val resultData = result.body()?.listStory ?: emptyList()
                LoadResult.Page(
                    data = resultData,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (resultData.isEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(Exception(result.toErrorResponse().message))
            }

        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}