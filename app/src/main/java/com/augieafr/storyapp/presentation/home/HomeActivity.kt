package com.augieafr.storyapp.presentation.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
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
import com.augieafr.storyapp.presentation.utils.dpToPx
import com.augieafr.storyapp.presentation.utils.setVisibility
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

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.userToken.collect {
                if (it.isEmpty()) {
                    startActivity(Intent(this@HomeActivity, AuthActivity::class.java))
                    finish()
                }
            }
        }

        viewModel.isFabExpanded.observe(this@HomeActivity) { isFabExpanded ->
            animateFab(isFabExpanded)
        }
    }

    private fun initUi() = with(binding) {
        supportFragmentManager.commit {
            add(
                fragmentContainer.id, ListStoryFragment.newInstance(
                    ::goToDetailActivity,
                    ::onListStoryScrollEvent
                )
            )
        }

        fabMoreAction.apply {
            setOnClickListener {
                viewModel.changeFabState()
            }
        }

        fabCreateStory.setOnClickListener {
            viewModel.changeFabState()
            launchAddStory.launch(Intent(this@HomeActivity, AddStoryActivity::class.java))
        }

        fabNearbyStory.setOnClickListener {
            viewModel.changeFabState()
            // TODO goToMapActivity
        }

        imgLogout.setOnClickListener {
            lifecycleScope.launch {
                viewModel.logout()
            }
        }
    }

    private fun onListStoryScrollEvent() = with(viewModel) {
        if (isFabExpanded.value == true) viewModel.changeFabState()
    }

    private fun animateFab(isExpanded: Boolean) = with(binding) {
        val firstViewToAnimate: ObjectAnimator
        val secondViewToAnimate: ObjectAnimator
        val animationDuration = 100L
        val fabMoreActionIcon: Int
        val onFirstAnimationEnd: () -> Unit
        var onSecondAnimationEnd: (() -> Unit)? = null

        if (isExpanded) {
            fabMoreActionIcon = R.drawable.baseline_keyboard_arrow_down_24
            fabCreateStory.setVisibility(true)
            onFirstAnimationEnd = {
                fabNearbyStory.setVisibility(true)
            }

            firstViewToAnimate =
                ObjectAnimator.ofFloat(fabCreateStory, View.TRANSLATION_Y, 8.dpToPx(), 0f).apply {
                    duration = animationDuration
                }

            secondViewToAnimate =
                ObjectAnimator.ofFloat(fabNearbyStory, View.TRANSLATION_Y, 8.dpToPx(), 0f).apply {
                    duration = animationDuration
                }
        } else {
            fabMoreActionIcon = R.drawable.baseline_keyboard_arrow_up_24
            onFirstAnimationEnd = {
                fabNearbyStory.setVisibility(false)
            }

            onSecondAnimationEnd = {
                fabCreateStory.setVisibility(false)
            }

            firstViewToAnimate =
                ObjectAnimator.ofFloat(fabNearbyStory, View.TRANSLATION_Y, 0f, 8.dpToPx()).apply {
                    duration = animationDuration
                }

            secondViewToAnimate =
                ObjectAnimator.ofFloat(fabCreateStory, View.TRANSLATION_Y, 0f, 8.dpToPx()).apply {
                    duration = animationDuration
                }
        }

        AnimatorSet().apply {
            playSequentially(firstViewToAnimate, secondViewToAnimate)
            firstViewToAnimate.doOnEnd {
                onFirstAnimationEnd.invoke()
            }
            secondViewToAnimate.doOnEnd {
                onSecondAnimationEnd?.invoke()
            }
            doOnEnd {
                fabMoreAction.setImageResource(fabMoreActionIcon)
            }
            start()
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