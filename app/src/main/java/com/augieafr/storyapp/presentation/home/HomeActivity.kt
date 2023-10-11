package com.augieafr.storyapp.presentation.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.augieafr.storyapp.databinding.ActivityHomeBinding
import com.augieafr.storyapp.presentation.add_story.AddStoryActivity
import com.augieafr.storyapp.presentation.list_story.ListStoryFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
    }

    private fun initUi() = with(binding) {
        supportFragmentManager.commit {
            replace(fragmentContainer.id, ListStoryFragment.newInstance())
        }

        fabAdd.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AddStoryActivity::class.java))
        }

        onBackPressedDispatcher.addCallback {
            // fixing shared element bug, finish activity right away when back button is pressed
            // no need to wait for transition animation to finish
            finish()
        }
    }
}