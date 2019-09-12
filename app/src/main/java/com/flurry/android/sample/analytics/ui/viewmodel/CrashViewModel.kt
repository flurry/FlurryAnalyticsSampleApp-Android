package com.flurry.android.sample.analytics.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flurry.android.FlurryAgent
import com.flurry.android.sample.analytics.util.SingleLiveEvent
import javax.inject.Inject

class CrashViewModel @Inject constructor(): ViewModel() {

    val displayToast: SingleLiveEvent<String> = SingleLiveEvent()

    val idErrorMessage: MutableLiveData<String?> = MutableLiveData()
    val messageErrorMessage: MutableLiveData<String?> = MutableLiveData()
    val classErrorMessage: MutableLiveData<String?> = MutableLiveData()
    val exceptionErrorMessage: MutableLiveData<String?> = MutableLiveData()
    val breadcrumbErrorMessage: MutableLiveData<String?> = MutableLiveData()

    fun validateThenLogErrorClass(errorId: String, errorMessage: String, errorClass: String) {
        if (errorId.isEmpty() || errorMessage.isEmpty() || errorClass.isEmpty()) {
            validate(errorId, idErrorMessage)
            validate(errorMessage, messageErrorMessage)
            validate(errorClass, classErrorMessage)
            return
        }

        displayToast.postValue("Log Error Class: errorId: $errorId, errorMessage: $errorMessage, errorClass: $errorClass")
        FlurryAgent.onError(errorId, errorMessage, errorClass)
        cleanErrorMessage()
    }

    fun validateThenLogException(errorId: String, errorMessage: String, exceptionMessage: String) {
        if (errorId.isEmpty() || errorMessage.isEmpty() || exceptionMessage.isEmpty()) {
            validate(errorId, idErrorMessage)
            validate(errorMessage, messageErrorMessage)
            validate(exceptionMessage, exceptionErrorMessage)
            return
        }

        try {
            throw RuntimeException(exceptionMessage)
        } catch (e: Exception) {
            displayToast.postValue("Log Exception: errorId: $errorId, errorMessage: $errorMessage, exceptionMessage: $exceptionMessage")
            FlurryAgent.onError(errorId, errorMessage, e)
        }

        cleanErrorMessage()
    }

    fun validateThenLogBreadcrumb(breadcrumb: String) {
        if (validate(breadcrumb, breadcrumbErrorMessage)) {
            displayToast.postValue("Log Breadcrumb: $breadcrumb")
            FlurryAgent.logBreadcrumb(breadcrumb)
            cleanErrorMessage()
        }
    }

    private fun validate(s: String, errorMessage: MutableLiveData<String?>): Boolean {
        return if (s.isEmpty()) {
            errorMessage.postValue("Can not be empty")
            false
        } else {
            errorMessage.postValue(null)
            true
        }
    }

    private fun cleanErrorMessage() {
        idErrorMessage.postValue(null)
        messageErrorMessage.postValue(null)
        classErrorMessage.postValue(null)
        exceptionErrorMessage.postValue(null)
        breadcrumbErrorMessage.postValue(null)
    }
}