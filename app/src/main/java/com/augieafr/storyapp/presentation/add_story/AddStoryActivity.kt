package com.augieafr.storyapp.presentation.add_story

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.augieafr.storyapp.R
import com.augieafr.storyapp.databinding.ActivityAddStoryBinding
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
    }

    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        lifecycleScope.launch {
            val bitmap = createAddPhotoPlaceholderBitmap()
            imgPhoto.setImageBitmap(bitmap)
        }
    }

    private suspend fun createAddPhotoPlaceholderBitmap(): Bitmap {
        while (
            binding.imgPhoto.width == 0 ||
            binding.imgPhoto.height == 0
        ) {
            // wait until the image view has been measured
            delay(10)
        }

        val width = binding.imgPhoto.width
        val height = binding.imgPhoto.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(
            MaterialColors.getColor(
                binding.imgPhoto,
                com.google.android.material.R.attr.colorSurfaceVariant
            )
        )

        canvas.drawText(
            getString(R.string.click_to_upload_your_photo), width / 2f, height / 2f,
            Paint().apply {
                color = MaterialColors.getColor(
                    binding.imgPhoto,
                    com.google.android.material.R.attr.colorOnSurfaceVariant
                )
                textAlign = Paint.Align.CENTER
                textSize = 64f
            }
        )

        return bitmap
    }

}