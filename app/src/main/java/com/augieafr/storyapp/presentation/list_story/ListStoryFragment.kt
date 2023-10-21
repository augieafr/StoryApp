package com.augieafr.storyapp.presentation.list_story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.augieafr.storyapp.R
import com.augieafr.storyapp.data.exceptions.NoDataException
import com.augieafr.storyapp.databinding.FragmentListStoryBinding
import com.augieafr.storyapp.presentation.utils.Alert
import com.augieafr.storyapp.presentation.utils.AlertType
import com.augieafr.storyapp.presentation.utils.PagingFooterLoadingStateAdapter
import com.augieafr.storyapp.presentation.utils.ViewModelProvider
import com.augieafr.storyapp.presentation.utils.setVisibility
import kotlinx.coroutines.launch

class ListStoryFragment : Fragment() {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ListStoryViewModel> {
        ViewModelProvider(requireContext())
    }

    private lateinit var pagingAdapter: ListStoryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStop() {
        // somehow when using shared element transition, the progress bar visibility reset to visible
        // when navigate to another activity. So, I need to set the visibility to false when onStop is called
        binding.progressBar.setVisibility(false)
        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initObserver()
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getAllStory().collect {
                    pagingAdapter.submitData(lifecycle, it)
                }
            }
        }
    }

    private fun initAdapter() = with(binding.rvStory) {
        pagingAdapter = ListStoryAdapter(onItemClickListener)
        layoutManager = LinearLayoutManager(requireContext())
        addLoadStateListener()

        adapter =
            pagingAdapter.withLoadStateFooter(footer = PagingFooterLoadingStateAdapter {
                lifecycleScope.launch {
                    pagingAdapter.retry()
                }
            })

        onScrollChangeListener?.let {
            setOnScrollChangeListener { _, _, _, _, _ ->
                it.invoke()
            }
        }
    }

    private fun addLoadStateListener() = with(binding) {
        pagingAdapter.addLoadStateListener {
            when (val refreshState = it.refresh) {
                is LoadState.Error -> {
                    progressBar.setVisibility(false)
                    if (refreshState.error is NoDataException) {
                        Alert.showAlert(
                            requireContext(),
                            AlertType.ERROR,
                            getString(R.string.no_story_found)
                        )
                    } else {
                        Alert.showAlert(
                            requireContext(),
                            AlertType.ERROR,
                            refreshState.error.message ?: getString(R.string.something_wrong)
                        )
                    }
                }

                LoadState.Loading -> progressBar.setVisibility(true)
                is LoadState.NotLoading -> progressBar.setVisibility(false)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        private lateinit var onItemClickListener: ((String) -> Unit)
        private var onScrollChangeListener: ((() -> Unit))? = null

        @JvmStatic
        fun newInstance(
            onItemClickListener: (String) -> Unit,
            onScrollChangeListener: ((() -> Unit))? = null
        ) =
            ListStoryFragment().apply {
                this@Companion.onItemClickListener = onItemClickListener
                this@Companion.onScrollChangeListener = onScrollChangeListener
            }
    }

}