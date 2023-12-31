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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.augieafr.storyapp.R
import com.augieafr.storyapp.data.utils.EspressoIdlingResource
import com.augieafr.storyapp.data.utils.ResultState
import com.augieafr.storyapp.databinding.ActivityAuthBinding
import com.augieafr.storyapp.presentation.home.HomeActivity
import com.augieafr.storyapp.presentation.utils.Alert
import com.augieafr.storyapp.presentation.utils.AlertType
import com.augieafr.storyapp.presentation.utils.AuthEditText
import com.augieafr.storyapp.presentation.utils.ViewModelProvider
import com.augieafr.storyapp.presentation.utils.setVisibility
import com.augieafr.storyapp.presentation.utils.showErrorAlert
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelProvider(this)
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    with(binding) {
                        merge(tilName.errorState, tilEmail.errorState, tilPassword.errorState)
                            .collect {
                                viewModel.updateFormErrorState(it)
                                setButtonEnabled()
                            }
                    }
                }

                viewModel.getToken.collect {
                    if (viewModel.firstTimeCheckToken.isCancelled && it.isNotEmpty()) {
                        Alert.showAlert(
                            this@AuthActivity,
                            AlertType.SUCCESS,
                            getString(R.string.login_successfully),
                        ) {
                            EspressoIdlingResource.decrement()
                            goToHomeScreen()
                        }
                    }

                    launch(viewModel.firstTimeCheckToken) {
                        if (it.isNotEmpty()) {
                            goToHomeScreen()
                        }
                        viewModel.firstTimeCheckToken.cancel()
                    }

                }
            }
        }

        viewModel.authUIState.observe(this) { state ->
            when (state) {
                is ResultState.Error -> state.throwable.showErrorAlert(this@AuthActivity)

                is ResultState.Loading -> binding.progressBar.setVisibility(state.isLoading)

                is ResultState.Success ->
                    Alert.showAlert(
                        this,
                        AlertType.SUCCESS,
                        getString(R.string.register_successfully),
                    ) {
                        changeScreenToLogin()
                    }
            }
        }
    }


    private fun setButtonEnabled() = with(viewModel) {
        if (isLoginScreen) {
            binding.btnContinue.isEnabled = formValidMap[AuthEditText.EMAIL_TYPE] == false &&
                    formValidMap[AuthEditText.PASSWORD_TYPE] == false
            return
        }
        binding.btnContinue.isEnabled = formValidMap[AuthEditText.NAME_TYPE] == false &&
                formValidMap[AuthEditText.EMAIL_TYPE] == false &&
                formValidMap[AuthEditText.PASSWORD_TYPE] == false
    }

    private fun onContinueButtonClicked() = with(binding) {
        btnContinue.setOnClickListener {
            if (viewModel.isLoginScreen) {
                viewModel.login(
                    tilEmail.text,
                    tilPassword.text
                )
            } else {
                viewModel.register(
                    tilName.text,
                    tilEmail.text,
                    tilPassword.text
                )
            }
        }
    }

    private fun goToHomeScreen() {
        Intent(this@AuthActivity, HomeActivity::class.java).also {
            this@AuthActivity.startActivity(it)
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
                        setButtonEnabled()
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