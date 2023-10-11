package com.augieafr.storyapp.presentation.utils

import android.widget.EditText

fun areFormsHaveError(
    vararg forms: EditText,
    additionalValidation: ((EditText) -> Unit)? = null
): Boolean {
    var hasError = false
    forms.forEach {
        additionalValidation?.invoke(it)
        if (it.error != null) {
            if (!hasError) hasError = true
            it.requestFocus()
        }
    }
    return hasError
}

fun EditText.errorIfEmpty(errorMessage: String) {
    if (text.toString().isEmpty()) {
        error = errorMessage
    }
}