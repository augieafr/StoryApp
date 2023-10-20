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
import androidx.recyclerview.widget.LinearLayoutManager
import com.augieafr.storyapp.R
import com.augieafr.storyapp.data.utils.ResultState
import com.augieafr.storyapp.databinding.FragmentListStoryBinding
import com.augieafr.storyapp.presentation.utils.Alert
import com.augieafr.storyapp.presentation.utils.AlertType
import com.augieafr.storyapp.presentation.utils.ViewModelProvider
import com.augieafr.storyapp.presentation.utils.setVisibility
import kotlinx.coroutines.launch

class ListStoryFragment : Fragment() {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ListStoryViewModel> {
        ViewModelProvider(requireContext())
    }

    private lateinit var adapter: ListStoryAdapter
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
                    when (it) {
                        is ResultState.Loading -> {
                            binding.progressBar.setVisibility(it.isLoading)
                        }

                        is ResultState.Success -> {
                            adapter.submitList(it.data)
                        }

                        is ResultState.Error -> {
                            if (it.errorMessage == NO_STORY_FOUND) {
                                Alert.showAlert(
                                    requireContext(),
                                    AlertType.ERROR,
                                    getString(R.string.no_story_found)
                                )
                            } else Alert.showAlert(
                                requireContext(),
                                AlertType.ERROR,
                                it.errorMessage
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initAdapter() = with(binding.rvStory) {
        this@ListStoryFragment.adapter = ListStoryAdapter(onItemClickListener)
        layoutManager = LinearLayoutManager(requireContext())
        adapter = this@ListStoryFragment.adapter
        onScrollChangeListener?.let {
            setOnScrollChangeListener { _, _, _, _, _ ->
                it.invoke()
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

        const val NO_STORY_FOUND = "no_story_found"
    }

}