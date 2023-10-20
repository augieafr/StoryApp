package com.augieafr.storyapp.presentation.detail_story

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.augieafr.storyapp.databinding.ActivityDetailStoryBinding
import com.augieafr.storyapp.presentation.utils.Alert
import com.augieafr.storyapp.presentation.utils.AlertType
import com.augieafr.storyapp.presentation.utils.ViewModelProvider
import com.augieafr.storyapp.presentation.utils.setVisibility
import com.bumptech.glide.Glide

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

        initObserver()
        getDetailUser()
    }

    private fun getDetailUser() {
        val id = intent.getStringExtra(EXTRA_STORY_ID)
        viewModel.getDetailStory(id.orEmpty())
    }

    private fun initObserver() = with(viewModel) {
        isLoading.observe(this@DetailStoryActivity) {
            binding.progressBar.setVisibility(it)
        }

        errorMessage.observe(this@DetailStoryActivity) {
            Alert.showAlert(this@DetailStoryActivity, AlertType.ERROR, it)
        }

        story.observe(this@DetailStoryActivity) {
            Glide.with(this@DetailStoryActivity)
                .load(it.photoUrl)
                .into(binding.imgPhoto)
            binding.tvName.text = it.name
            binding.tvDescription.text = it.description
        }
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}