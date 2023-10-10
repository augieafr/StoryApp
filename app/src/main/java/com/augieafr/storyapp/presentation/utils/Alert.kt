package com.augieafr.storyapp.presentation.utils

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.augieafr.storyapp.R
import com.augieafr.storyapp.databinding.CustomAlertLayoutBinding
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Alert {
    fun showAlert(
        context: Context,
        alertType: AlertType,
        msg: String,
        duration: Long = 2000,
        action: (() -> Unit)? = null
    ) {
        Dialog(context, R.style.custom_alert).apply {
            val binding = CustomAlertLayoutBinding.inflate(LayoutInflater.from(context))
            val alertJob = Job()
            setContentView(binding.root)

            window?.setGravity(Gravity.TOP)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setCanceledOnTouchOutside(true)
            with(binding) {
                root.setOnClickListener {
                    dismiss()
                }
                if (alertType == AlertType.SUCCESS) {
                    tvAlert.setBackgroundColor(
                        MaterialColors.getColor(
                            tvAlert,
                            androidx.appcompat.R.attr.colorPrimary
                        )
                    )
                    tvAlert.setTextColor(
                        MaterialColors.getColor(
                            tvAlert,
                            com.google.android.material.R.attr.colorOnPrimary
                        )
                    )
                } else {
                    tvAlert.setBackgroundColor(
                        MaterialColors.getColor(
                            tvAlert,
                            androidx.appcompat.R.attr.colorError
                        )
                    )
                    tvAlert.setTextColor(
                        MaterialColors.getColor(
                            tvAlert,
                            com.google.android.material.R.attr.colorOnError
                        )
                    )
                }
                tvAlert.text = msg
            }

            setOnCancelListener {
                alertJob.cancel()
                action?.invoke()
            }

            CoroutineScope(Dispatchers.Main + alertJob).launch {
                show()
                delay(duration)
                dismiss()
                action?.invoke()
                this.cancel()
            }
        }

    }
}

enum class AlertType {
    SUCCESS,
    ERROR
}