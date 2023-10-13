package com.augieafr.storyapp.presentation.list_story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.augieafr.storyapp.R
import com.augieafr.storyapp.databinding.FragmentListStoryBinding
import com.augieafr.storyapp.presentation.utils.Alert
import com.augieafr.storyapp.presentation.utils.AlertType
import com.augieafr.storyapp.presentation.utils.ViewModelProvider
import com.augieafr.storyapp.presentation.utils.setLoadingVisibility

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
        binding.progressBar.setLoadingVisibility(false)
        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initObserver()
    }

    private fun initObserver() {
        viewModel.listStory.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.setLoadingVisibility(it)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            if (it == NO_STORY_FOUND) {
                Alert.showAlert(
                    requireContext(),
                    AlertType.ERROR,
                    getString(R.string.no_story_found)
                )
                return@observe
            }
            Alert.showAlert(requireContext(), AlertType.ERROR, it)
        }
    }

    private fun initAdapter() = with(binding) {
        adapter = ListStoryAdapter()
        rvStory.layoutManager = LinearLayoutManager(requireContext())
        rvStory.adapter = this@ListStoryFragment.adapter
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ListStoryFragment()

        const val NO_STORY_FOUND = "no_story_found"
    }

}