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
import androidx.core.widget.addTextChangedListener
import com.augieafr.storyapp.R
import com.augieafr.storyapp.data.local.preferences.UserPreference
import com.augieafr.storyapp.data.local.preferences.dataStore
import com.augieafr.storyapp.databinding.ActivityAuthBinding
import com.augieafr.storyapp.presentation.home.HomeActivity
import com.augieafr.storyapp.presentation.utils.Alert
import com.augieafr.storyapp.presentation.utils.AlertType
import com.augieafr.storyapp.presentation.utils.ViewModelProvider
import com.augieafr.storyapp.presentation.utils.areFormsHaveError
import com.augieafr.storyapp.presentation.utils.isEmpty

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelProvider(UserPreference.getInstance(this.dataStore))
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
        setupTextInput()
        onContinueButtonClicked()
    }

    private fun initObserver() {
        viewModel.isError.observe(this) {
            if (!it.isNullOrEmpty()) {
                Alert.showAlert(
                    this,
                    AlertType.ERROR,
                    it
                )
            }
        }

        viewModel.isSuccessAuthentication.observe(this) {
            if (viewModel.isLoginScreen) {
                Alert.showAlert(
                    this,
                    AlertType.SUCCESS,
                    getString(R.string.login_successfully),
                ) {
                    goToHomeScreen()
                }
            } else {
                Alert.showAlert(
                    this,
                    AlertType.SUCCESS,
                    getString(R.string.register_successfully),
                ) {
                    changeScreenToLogin()
                }
            }
        }

        viewModel.isLoading.observe(this) {
            setLoadingIndicator(it)
        }
    }

    private fun onContinueButtonClicked() = with(binding) {
        btnContinue.setOnClickListener {
            if (viewModel.isLoginScreen) {
                val areFormsHaveError = areFormsHaveError(
                    edtEmail,
                    edtPassword
                ) {
                    it.isEmpty(getString(R.string.field_must_not_be_empty))
                }
                if (!areFormsHaveError) viewModel.login(
                    edtEmail.text.toString(),
                    edtPassword.text.toString()
                )
            } else {
                val areFormsHaveError = areFormsHaveError(
                    edtName,
                    edtEmail,
                    edtPassword
                ) {
                    it.isEmpty(getString(R.string.field_must_not_be_empty))
                }
                if (!areFormsHaveError) viewModel.register(
                    edtName.text.toString(),
                    edtEmail.text.toString(),
                    edtPassword.text.toString()
                )
            }
        }
    }

    private fun setupTextInput() = with(binding) {
        edtPassword.addTextChangedListener(
            afterTextChanged = {
                if (it.toString().length < 8) {
                    edtPassword.error = getString(R.string.password_must_be_at_least_8_characters)
                } else {
                    edtPassword.error = null
                }
            }
        )

        edtEmail.addTextChangedListener(
            afterTextChanged = {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()) {
                    edtEmail.error = getString(R.string.email_not_valid)
                } else {
                    edtEmail.error = null
                }
            }
        )
    }

    private fun goToHomeScreen() {
        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, binding.imgLogo, "transitionLogo"
            )
        Intent(this@AuthActivity, HomeActivity::class.java).also {
            this@AuthActivity.startActivity(it, optionsCompat.toBundle())
        }
        finish()
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

    private fun setLoadingIndicator(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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