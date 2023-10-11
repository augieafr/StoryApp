package com.augieafr.storyapp.presentation.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.augieafr.storyapp.R
import com.augieafr.storyapp.data.local.preferences.UserPreference
import com.augieafr.storyapp.data.local.preferences.dataStore
import com.augieafr.storyapp.data.remote.ApiConfig
import com.augieafr.storyapp.databinding.ActivityAuthBinding
import com.augieafr.storyapp.presentation.home.HomeActivity
import com.augieafr.storyapp.presentation.utils.Alert
import com.augieafr.storyapp.presentation.utils.AlertType
import com.augieafr.storyapp.presentation.utils.ViewModelProvider
import com.augieafr.storyapp.presentation.utils.areFormsHaveError
import com.augieafr.storyapp.presentation.utils.errorIfEmpty
import com.augieafr.storyapp.presentation.utils.setLoadingVisibility
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelProvider(UserPreference.getInstance(this.dataStore), ApiConfig.getApiService())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObserver()
        initView()
    }

    private fun initView() {
        setupRegisterWording()
        onContinueButtonClicked()
    }

    private fun initObserver() {
        val firstTimeCheckToken = Job()
        lifecycleScope.launch {
            viewModel.getToken.collect {
                if (firstTimeCheckToken.isCancelled && it.isNotEmpty()) {
                    Alert.showAlert(
                        this@AuthActivity,
                        AlertType.SUCCESS,
                        getString(R.string.login_successfully),
                    ) {
                        goToHomeScreen()
                    }
                }

                launch(firstTimeCheckToken) {
                    if (it.isNotEmpty()) {
                        goToHomeScreen()
                    }
                    firstTimeCheckToken.cancel()
                }

            }
        }

        viewModel.isError.observe(this) {
            if (!it.isNullOrEmpty()) {
                Alert.showAlert(
                    this,
                    AlertType.ERROR,
                    it
                )
            }
        }

        viewModel.isSuccessRegister.observe(this) {
            Alert.showAlert(
                this,
                AlertType.SUCCESS,
                getString(R.string.register_successfully),
            ) {
                changeScreenToLogin()
            }
        }

        viewModel.isLoading.observe(this) {
            binding.progressBar.setLoadingVisibility(it)
        }
    }

    private fun onContinueButtonClicked() = with(binding) {
        btnContinue.setOnClickListener {
            if (viewModel.isLoginScreen) {
                val areFormsHaveError = areFormsHaveError(
                    tilEmail.editText,
                    tilPassword.editText
                ) {
                    it.errorIfEmpty(getString(R.string.field_must_not_be_empty))
                }
                if (!areFormsHaveError) viewModel.login(
                    tilEmail.text,
                    tilPassword.text
                )
            } else {
                val areFormsHaveError = areFormsHaveError(
                    tilName.editText,
                    tilEmail.editText,
                    tilPassword.editText
                ) {
                    it.errorIfEmpty(getString(R.string.field_must_not_be_empty))
                }
                if (!areFormsHaveError) viewModel.register(
                    tilName.text,
                    tilEmail.text,
                    tilPassword.text
                )
            }
        }
    }

    private fun goToHomeScreen() {
        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, binding.imgLogo, "transitionLogo"
            )
        Intent(this@AuthActivity, HomeActivity::class.java).also {
            this@AuthActivity.startActivity(it, optionsCompat.toBundle())
        }
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
                        if (viewModel.isLoginScreen) changeScreenToRegister()
                        else changeScreenToLogin()
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

    private fun changeScreenToLogin() {
        viewModel.isLoginScreen = true
        binding.root.transitionToStart()
        binding.btnContinue.text = getString(R.string.login)
        setupRegisterWording()
    }

    private fun changeScreenToRegister() {
        viewModel.isLoginScreen = false
        binding.root.transitionToEnd()
        binding.btnContinue.text = getString(R.string.register)
        setupRegisterWording()
    }
}