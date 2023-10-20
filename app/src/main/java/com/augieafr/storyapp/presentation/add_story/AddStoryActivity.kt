package com.augieafr.storyapp.presentation.add_story

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.augieafr.storyapp.R
import com.augieafr.storyapp.databinding.ActivityAddStoryBinding
import com.augieafr.storyapp.databinding.BottomSheetDialogImagePickerBinding
import com.augieafr.storyapp.presentation.utils.Alert
import com.augieafr.storyapp.presentation.utils.AlertType
import com.augieafr.storyapp.presentation.utils.ViewModelProvider
import com.augieafr.storyapp.presentation.utils.getImageUri
import com.augieafr.storyapp.presentation.utils.setVisibility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelProvider(this)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.storyImageUri = uri
            setStoryImage()
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) setStoryImage()
        else viewModel.storyImageUri = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        initObserver()
    }

    private fun initObserver() = with(viewModel) {
        isLoading.observe(this@AddStoryActivity) {
            binding.progressBar.setVisibility(it)
        }

        errorMessage.observe(this@AddStoryActivity) {
            Alert.showAlert(
                this@AddStoryActivity,
                AlertType.ERROR,
                it
            )
        }

        isSuccessUpload.observe(this@AddStoryActivity) {
            if (it) {
                setResult(RESULT_SUCCESS_UPLOAD_STORY)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setStoryImage()
    }

    private fun setStoryImage() {
        viewModel.storyImageUri?.let {
            binding.imgPhoto.setImageURI(it)
        }
    }

    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (viewModel.storyImageUri == null) lifecycleScope.launch {
            val bitmap = createAddPhotoPlaceholderBitmap()
            imgPhoto.setImageBitmap(bitmap)
        }

        imgPhoto.setOnClickListener {
            // show dialog to choose image from gallery or camera
            showImagePickerDialog()
        }

        tvNext.setOnClickListener {
            with(viewModel) {
                with(binding.edtDescription) {
                    if (text.toString().isEmpty()) {
                        error = getString(R.string.field_must_not_be_empty)
                        requestFocus()
                        return@setOnClickListener
                    }
                }
                storyImageUri?.let {
                    uploadStory(it, edtDescription.text.toString(), this@AddStoryActivity)
                } ?: run {
                    Alert.showAlert(
                        this@AddStoryActivity,
                        AlertType.ERROR,
                        getString(R.string.please_upload_your_photo)
                    )
                }
            }
        }

        edtDescription.addTextChangedListener(afterTextChanged = {
            if (it.toString().isNotEmpty()) {
                edtDescription.error = null
            } else {
                edtDescription.error = getString(R.string.field_must_not_be_empty)
            }
        })
    }

    private fun showImagePickerDialog() {
        val bottomSheetBinding = BottomSheetDialogImagePickerBinding.inflate(layoutInflater)
        BottomSheetDialog(this).apply {
            setContentView(bottomSheetBinding.root)
            with(bottomSheetBinding) {
                imgCamera.setOnClickListener {
                    intentToCamera()
                    dismiss()
                }
                imgGallery.setOnClickListener {
                    intentToGallery()
                    dismiss()
                }
            }
            show()
        }
    }

    private fun intentToGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun intentToCamera() {
        viewModel.storyImageUri = getImageUri(this)
        launcherIntentCamera.launch(viewModel.storyImageUri)
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

    companion object {
        const val RESULT_SUCCESS_UPLOAD_STORY = 100
    }
}