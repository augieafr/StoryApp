package com.augieafr.storyapp.presentation.auth

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.augieafr.storyapp.R
import com.augieafr.storyapp.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        setupRegisterWording()
    }

    private fun setupRegisterWording() {
        val fullText =
            if (viewModel.isLoginScreen) {
                getString(R.string.auth_screen_dont_have_account_wording)
            } else getString(
                R.string.auth_screen_already_have_account_wording
            )
        val startIndex = fullText.lastIndexOf(".") + 2
        val endIndex = fullText.length

        val spannableString = SpannableString(fullText).apply {
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        with(binding) {
                            if (viewModel.isLoginScreen) {
                                viewModel.isLoginScreen = false
                                root.transitionToEnd()
                                btnContinue.text = getString(R.string.register)
                                setupRegisterWording()
                            } else {
                                viewModel.isLoginScreen = true
                                root.transitionToStart()
                                btnContinue.text = getString(R.string.login)
                                setupRegisterWording()
                            }
                        }
                    }

                },
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        with(binding.txtMode) {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }
}