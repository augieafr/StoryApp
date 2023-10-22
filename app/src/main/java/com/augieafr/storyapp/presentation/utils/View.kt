package com.augieafr.storyapp.presentation.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import com.augieafr.storyapp.R
import com.augieafr.storyapp.data.exceptions.NoDataException


fun View.setVisibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun Int.dpToPx(): Float {
    return (this * (Resources.getSystem()
        .displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

fun Throwable.showErrorAlert(context: Context) {
    if (this is NoDataException) {
        Alert.showAlert(
            context,
            AlertType.ERROR,
            context.getString(R.string.no_story_found)
        )
    } else {
        Alert.showAlert(
            context,
            AlertType.ERROR,
            this.message ?: context.getString(R.string.something_wrong)
        )
    }
}