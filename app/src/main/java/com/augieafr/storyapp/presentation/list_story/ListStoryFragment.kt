package com.augieafr.storyapp.presentation.list_story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.augieafr.storyapp.data.local.preferences.UserPreference
import com.augieafr.storyapp.data.local.preferences.dataStore
import com.augieafr.storyapp.data.remote.ApiConfig
import com.augieafr.storyapp.databinding.FragmentListStoryBinding
import com.augieafr.storyapp.presentation.utils.ViewModelProvider

class ListStoryFragment : Fragment() {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ListStoryViewModel> {
        ViewModelProvider(
            UserPreference.getInstance(requireContext().dataStore),
            ApiConfig.getApiService()
        )
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initObserver()
    }

    private fun initObserver() {
        viewModel.listStory.observe(viewLifecycleOwner) {
            adapter.submitList(it)
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
    }

}