package com.augieafr.storyapp.presentation.home

import android.content.Intent
import android.os.Bundle
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
            add(fragmentContainer.id, ListStoryFragment.newInstance())
        }

        fabAdd.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AddStoryActivity::class.java))
        }
    }
}