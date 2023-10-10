package com.augieafr.storyapp.presentation.utils

import android.view.View
import android.widget.ProgressBar

fun ProgressBar.setLoadingVisibility(isLoading: Boolean) {
    visibility = if (isLoading) View.VISIBLE else View.GONE
}