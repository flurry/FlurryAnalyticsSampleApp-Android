package com.flurry.android.sample.analytics.util

import com.google.android.material.textfield.TextInputLayout

class Utils {
    companion object {
        fun handleErrorMessage(errorLayout: TextInputLayout, errorMessage: String?) {
            errorLayout.isErrorEnabled = errorMessage != null
            errorLayout.error = errorMessage
        }
    }
}