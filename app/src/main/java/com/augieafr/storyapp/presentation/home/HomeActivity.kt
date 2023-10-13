package com.augieafr.storyapp.presentation.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.augieafr.storyapp.R
import com.augieafr.storyapp.databinding.ActivityHomeBinding
import com.augieafr.storyapp.presentation.add_story.AddStoryActivity
import com.augieafr.storyapp.presentation.list_story.ListStoryFragment
import com.augieafr.storyapp.presentation.utils.Alert
import com.augieafr.storyapp.presentation.utils.AlertType

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val launchAddStory =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AddStoryActivity.RESULT_SUCCESS_UPLOAD_STORY) {
                Alert.showAlert(
                    this,
                    AlertType.SUCCESS,
                    getString(R.string.success_upload_story)
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
    }

    private fun initUi() = with(binding) {
        supportFragmentManager.commit {
            add(fragmentContainer.id, ListStoryFragment.newInstance())
        }

        fabAdd.setOnClickListener {
            launchAddStory.launch(Intent(this@HomeActivity, AddStoryActivity::class.java))
        }
    }
}