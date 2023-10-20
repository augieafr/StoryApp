package com.augieafr.storyapp.presentation.utils

import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View


fun View.setVisibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun Int.dpToPx(): Float {
    return (this * (Resources.getSystem()
        .displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}