package com.flurry.android.sample.analytics.ui.viewmodel

import android.text.InputType
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flurry.android.Consent
import com.flurry.android.FlurryAgent
import com.flurry.android.FlurryConsent
import com.flurry.android.FlurryPrivacySession
import com.flurry.android.sample.analytics.AnalyticsSampleApplication
import com.flurry.android.sample.analytics.util.SingleLiveEvent
import javax.inject.Inject

class OtherFeaturesViewModel @Inject constructor() : ViewModel() {

    val displayToast: SingleLiveEvent<String> = SingleLiveEvent()

    val errorMessage1: MutableLiveData<String?> = MutableLiveData()
    val errorMessage2: MutableLiveData<String?> = MutableLiveData()
    val errorMessage3: MutableLiveData<String?> = MutableLiveData()

    val hint1: MutableLiveData<Pair<String, Int>> = MutableLiveData()
    val hint2: MutableLiveData<Pair<String, Int>> = MutableLiveData()
    val hint3: MutableLiveData<Pair<String, Int>> = MutableLiveData()

    val answerSpinnerType: MutableLiveData<SpinnerType> = MutableLiveData()

    val resultText: MutableLiveData<String> = MutableLiveData()

    fun construct(pos: Int) {
        when (pos) {
            3 -> postHint(hint1, "Version Name")
            4 -> answerSpinnerType.postValue(SpinnerType.BOOL)
            5 -> {
                postHint(hint1, "Origin Name")
                postHint(hint2, "Origin Version")
                postHint(hint3, "Number Of Parameters", InputType.TYPE_CLASS_NUMBER)
            }
            6 -> postHint(hint1, "Instant App Name")
            9 -> answerSpinnerType.postValue(SpinnerType.CONSENT)
            13 -> postHint(hint1, "Age", InputType.TYPE_CLASS_NUMBER)
            14 -> answerSpinnerType.postValue(SpinnerType.GENDER)
            15 -> postHint(hint1, "User Id")
            16 -> {
                postHint(hint1, "Origin Name")
                postHint(hint2, "Deep Link")
            }
            17 -> {
                postHint(hint1, "Name")
                postHint(hint2, "Value")
            }
        }
    }

    fun validateThenRun(
        featurePos: Int,
        input1: String,
        input2: String,
        input3: String,
        answerPos: Int
    ) {
        when (featurePos) {
            0 -> resultText.postValue(FlurryAgent.getAgentVersion().toString())
            1 -> resultText.postValue(FlurryAgent.getReleaseVersion())
            2 -> getAddOnModules()
            3 -> validateThenLogToFlurry(input1, "Version name can not be empty") {
                FlurryAgent.setVersionName(input1)
            }
            4 -> FlurryAgent.setReportLocation(answerPos == 0).also { notifyResult(true) }
            5 -> validateAndAddOrigin(input1, input2, input3)
            6 -> validateThenLogToFlurry(input1, "Instant app name can not be empty") {
                FlurryAgent.setInstantAppName(input1)
            }
            7 -> resultText.postValue(FlurryAgent.getInstantAppName().toString())
            8 -> resultText.postValue(generateConsentString(FlurryAgent.getFlurryConsent()))
            9 -> createFlurryConsent(answerPos)?.let {
                FlurryAgent.updateFlurryConsent(it)
                notifyResult(true)
            }
            10 -> resultText.postValue(FlurryAgent.isSessionActive().toString())
            11 -> resultText.postValue(FlurryAgent.getSessionId())
            12 -> FlurryAgent.onPageView().also { notifyResult(true) }
            13 -> validateThenLogToFlurry(input1, "Age can not be empty") {
                FlurryAgent.setAge(input1.toInt())
            }
            14 -> FlurryAgent.setGender(getGenderByte(answerPos)).also { notifyResult(true) }
            15 -> validateThenLogToFlurry(input1, "User id can not be empty") {
                FlurryAgent.setUserId(input1)
            }
            16 -> validateThenLogToFlurry(input1, "Origin name can not be empty") {
                FlurryAgent.setSessionOrigin(input1, input2)
            }
            17 -> validateThenLogToFlurry(input1, "Name can not be empty") {
                FlurryAgent.addSessionProperty(input1, input2)
            }
            18 -> FlurryAgent.openPrivacyDashboard(FlurryPrivacySession.Request(AnalyticsSampleApplication.context, object : FlurryPrivacySession.Callback {
                override fun success() {
                    displayToast.setValue("Privacy Dashboard opened successfully")
                }

                override fun failure() {
                    displayToast.setValue("Opening Privacy Dashboard failed")
                }
            }))
        }
    }

    private fun getAddOnModules() {
        val modules = FlurryAgent.getAddOnModules()
        resultText.postValue("There are ${modules.size} add-on module(s)")
    }

    private fun validateAndAddOrigin(
        originName: String,
        originVersion: String,
        paramsCount: String
    ) {
        if (originName.isEmpty() || originVersion.isEmpty()) {
            if (originName.isEmpty()) {
                errorMessage1.postValue("Origin name can not be empty")
            }

            if (originVersion.isEmpty()) {
                errorMessage2.postValue("Origin version can not be empty")
            }

            notifyResult(false)
            return
        }

        errorMessage1.postValue(null)
        errorMessage2.postValue(null)

        val count = if (paramsCount.isEmpty()) 0 else paramsCount.toInt()

        if (count == 0) {
            FlurryAgent.addOrigin(originName, originVersion)
        } else {
            FlurryAgent.addOrigin(originName, originVersion, mockOriginParams(count))
        }

        notifyResult(true)
    }

    private fun mockOriginParams(count: Int): Map<String, String> {
        val map = HashMap<String, String>()

        for (i in 1..count) {
            map["key$i"] = "value$i"
        }

        return map
    }

    private fun generateConsentString(consent: Consent?) =
        if (consent == null) "null" else "isGdprScope: " + consent.isGdprScope + ", consentStrings: " + consent.consentStrings

    private fun createFlurryConsent(pos: Int) =
        when (pos) {
            1 -> FlurryConsent(true, mapOf("iab" to IAB_SAMPLE_STRING))
            2 -> FlurryConsent(false, emptyMap())
            else -> null
        }

    private fun getGenderByte(pos: Int): Byte =
        when (pos) {
            0 -> 1
            1 -> 0
            else -> -1
        }

    private fun validateThenLogToFlurry(input: String, error: String, runnable: () -> Unit) {
        if (input.isEmpty()) {
            errorMessage1.postValue(error)
            notifyResult(true)

            return
        }

        errorMessage1.postValue(null)

        runnable.invoke()

        notifyResult(true)
    }

    private fun postHint(
        liveData: MutableLiveData<Pair<String, Int>>,
        hint: String,
        inputType: Int = InputType.TYPE_CLASS_TEXT
    ) {
        liveData.postValue(Pair(hint, inputType))
    }

    private fun notifyResult(isSuccess: Boolean) = displayToast.setValue(if(isSuccess) "Success" else "Error")

    enum class SpinnerType {
        BOOL, GENDER, CONSENT
    }

    companion object {
        private const val IAB_SAMPLE_STRING = "BOEFEAyOEFEAyAHABDENAI4AAAB9vABAASA"
    }
}