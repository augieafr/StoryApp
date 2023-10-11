package com.augieafr.storyapp.presentation.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import com.augieafr.storyapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AuthEditText(context: Context, attributeSet: AttributeSet) :
    TextInputLayout(context, attributeSet) {

    private var authInputType = 0
    val editText = TextInputEditText(context)
    val text: String get() = editText.text.toString()

    init {
        initAttrs(attributeSet)
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.AuthEditText
        )

        try {
            authInputType = typedArray.getInt(R.styleable.AuthEditText_auth_input_type, 0)
        } finally {
            typedArray.recycle()
        }

        setup()
    }

    private fun setup() {
        addView(editText)
        when (authInputType) {
            PASSWORD_TYPE -> {
                hint = context.getString(R.string.password)
                with(editText) {
                    inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
                    addTextChangedListener(afterTextChanged = {
                        error = if (it.toString().length < 8) {
                            context.getString(R.string.password_must_be_at_least_8_characters)
                        } else {
                            null
                        }
                    })
                }
            }

            EMAIL_TYPE -> {
                hint = context.getString(R.string.email)
                with(editText) {
                    inputType = EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    addTextChangedListener(afterTextChanged = {
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()) {
                            editText.error = context.getString(R.string.email_not_valid)
                        } else {
                            editText.error = null
                        }
                    })
                }
            }

            else -> {
                Log.d("AuthEditText", "type: $authInputType")
                hint = hint ?: context.getString(R.string.name)
            }
        }
    }

    companion object {
        private const val EMAIL_TYPE = 1
        private const val PASSWORD_TYPE = 2
    }
}