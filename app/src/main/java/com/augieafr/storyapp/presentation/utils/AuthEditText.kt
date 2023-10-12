package com.augieafr.storyapp.presentation.utils

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import com.augieafr.storyapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.MutableStateFlow

class AuthEditText(context: Context, attributeSet: AttributeSet) :
    TextInputLayout(context, attributeSet) {

    private var authInputType = 0
    private val editText = TextInputEditText(this.context)
    var text: String
        get() = editText.text.toString()
        set(value) {
            editText.setText(value)
        }

    lateinit var errorState: MutableStateFlow<Pair<Int, Boolean>>

    init {
        initAttrs(attributeSet)
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.AuthEditText
        )

        try {
            authInputType = typedArray.getInt(R.styleable.AuthEditText_auth_input_type, NAME_TYPE)
            errorState = MutableStateFlow(Pair(authInputType, true))
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
                            errorState.value = errorState.value.copy(second = true)
                            context.getString(R.string.password_must_be_at_least_8_characters)
                        } else {
                            errorState.value = errorState.value.copy(second = false)
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
                        error =
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString())
                                    .matches()
                            ) {
                                errorState.value = errorState.value.copy(second = true)
                                context.getString(R.string.email_not_valid)
                            } else {
                                errorState.value = errorState.value.copy(second = false)
                                null
                            }
                    })
                }
            }

            else -> {
                hint = hint ?: context.getString(R.string.name)
                with(editText) {
                    addTextChangedListener(afterTextChanged = {
                        error =
                            if (this@AuthEditText.text.isEmpty()
                            ) {
                                errorState.value = errorState.value.copy(second = true)
                                context.getString(R.string.field_must_not_be_empty)
                            } else {
                                errorState.value = errorState.value.copy(second = false)
                                null
                            }
                    })
                }
            }
        }
    }

    companion object {
        const val NAME_TYPE = 0
        const val EMAIL_TYPE = 1
        const val PASSWORD_TYPE = 2
    }
}