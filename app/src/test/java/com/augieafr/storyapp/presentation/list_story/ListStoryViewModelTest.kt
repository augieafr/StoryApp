package com.augieafr.storyapp.presentation.list_story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.augieafr.storyapp.data.repository.StoryRepository
import com.augieafr.storyapp.presentation.model.StoryUIModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ListStoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var storyViewModel: ListStoryViewModel

    @Before
    fun before() {
        storyViewModel = ListStoryViewModel(storyRepository)
    }

    @Test
    fun `get success paging list story`() = runTest {
        val dummyStory = DataDummy.generateDummyListStoryUIModel()
        val data = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = flowOf(data)

        `when`(storyRepository.getPagingStories()).thenReturn(expectedStory)

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        val actualData = storyViewModel.getAllStory().first()
        differ.submitData(actualData)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `get paging list story that return 0 data`() = runTest {
        val dummyStory = emptyList<StoryUIModel>()
        val data = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = flowOf(data)

        `when`(storyRepository.getPagingStories()).thenReturn(expectedStory)

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        val actualData = storyViewModel.getAllStory().first()
        differ.submitData(actualData)

        assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, Flow<List<StoryUIModel>>>() {
    companion object {
        fun snapshot(items: List<StoryUIModel>): PagingData<StoryUIModel> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Flow<List<StoryUIModel>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Flow<List<StoryUIModel>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}