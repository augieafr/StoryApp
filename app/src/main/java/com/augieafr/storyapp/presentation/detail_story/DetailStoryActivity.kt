package com.augieafr.storyapp.presentation.detail_story

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.augieafr.storyapp.data.model.response.Story
import com.augieafr.storyapp.data.utils.ResultState
import com.augieafr.storyapp.databinding.ActivityDetailStoryBinding
import com.augieafr.storyapp.presentation.utils.Alert
import com.augieafr.storyapp.presentation.utils.AlertType
import com.augieafr.storyapp.presentation.utils.ViewModelProvider
import com.augieafr.storyapp.presentation.utils.setVisibility
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel by viewModels<DetailStoryViewModel> {
        ViewModelProvider(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        getDetailUser()
    }

    private fun getDetailUser() {
        val id = intent.getStringExtra(EXTRA_STORY_ID)
        lifecycleScope.launch {
            viewModel.getDetailStory(id.orEmpty()).collect {
                handleResultState(it)
            }
        }
    }

    private fun handleResultState(state: ResultState<Story>) {
        when (state) {
            is ResultState.Loading -> {
                binding.progressBar.setVisibility(state.isLoading)
            }

            is ResultState.Success -> {
                binding.progressBar.setVisibility(false)
                Glide.with(this@DetailStoryActivity)
                    .load(state.data.photoUrl)
                    .into(binding.imgPhoto)
                binding.tvName.text = state.data.name
                binding.tvDescription.text = state.data.description
            }

            is ResultState.Error -> {
                Alert.showAlert(this@DetailStoryActivity, AlertType.ERROR, state.errorMessage)
            }
        }
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}