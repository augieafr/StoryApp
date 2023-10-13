package com.augieafr.storyapp.presentation.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.augieafr.storyapp.R
import com.augieafr.storyapp.databinding.ActivityHomeBinding
import com.augieafr.storyapp.presentation.add_story.AddStoryActivity
import com.augieafr.storyapp.presentation.auth.AuthActivity
import com.augieafr.storyapp.presentation.detail_story.DetailStoryActivity
import com.augieafr.storyapp.presentation.list_story.ListStoryFragment
import com.augieafr.storyapp.presentation.utils.Alert
import com.augieafr.storyapp.presentation.utils.AlertType
import com.augieafr.storyapp.presentation.utils.ViewModelProvider
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelProvider(
            this
        )
    }
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
        initObserver()
    }

    private fun initObserver() = lifecycleScope.launch {
        viewModel.userToken.collect {
            if (it.isEmpty()) {
                startActivity(Intent(this@HomeActivity, AuthActivity::class.java))
                finish()
            }
        }
    }

    private fun initUi() = with(binding) {
        supportFragmentManager.commit {
            add(
                fragmentContainer.id, ListStoryFragment.newInstance(
                    ::goToDetailActivity
                )
            )
        }

        fabAdd.setOnClickListener {
            launchAddStory.launch(Intent(this@HomeActivity, AddStoryActivity::class.java))
        }

        imgLogout.setOnClickListener {
            lifecycleScope.launch {
                viewModel.logout()
            }
        }
    }

    private fun goToDetailActivity(id: String) {
        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, binding.imgLogo, getString(R.string.logo_transition)
            )
        val intent = Intent(this, DetailStoryActivity::class.java)
            .putExtra(DetailStoryActivity.EXTRA_STORY_ID, id)
        startActivity(intent, optionsCompat.toBundle())
    }
}