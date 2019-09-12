package com.flurry.android.sample.analytics.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flurry.android.FlurryAgent
import com.flurry.android.FlurryEventRecordStatus
import com.flurry.android.sample.analytics.util.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EventViewModel @Inject constructor() : ViewModel() {
    var eventName: String? = null
        set(value) {
            field = value

            if (value.isNullOrEmpty()) {
                isEventNameValid.postValue("event name is missing")
            } else {
                isEventNameValid.postValue(null)
            }
        }

    val isEventNameValid = MutableLiveData<String?>()
    val isTimedEvent = MutableLiveData<Boolean>()

    var delayTime: Long = -1
        set(value) {
            field = value

            if (value <= 0) {
                isDelayTimeValid.postValue("delay not valid")
            } else {
                isDelayTimeValid.postValue(null)
            }
        }

    val isDelayTimeValid = MutableLiveData<String?>()

    val displayToast: SingleLiveEvent<String> = SingleLiveEvent()
    val paramCount: SingleLiveEvent<Int> = SingleLiveEvent()
    val keyErrorMessage = SingleLiveEvent<String?>()
    val valueErrorMessage = SingleLiveEvent<String?>()

    val startParams: MutableList<Pair<String, String>> = ArrayList()
    val endParams: MutableList<Pair<String, String>> = ArrayList()

    private val compositeDisposable = CompositeDisposable()

    init {
        isTimedEvent.value = false
    }

    fun logEvent() {
        if (eventName.isNullOrEmpty()) {
            isEventNameValid.postValue("event name is missing")
            displayToast.setValue("Event Input is invalid")
            return
        }

        val isTimedEvent = this.isTimedEvent.value!!

        if (isTimedEvent && delayTime <= 0) {
            isDelayTimeValid.postValue("delay not valid")
            return
        }

        val startParams = startParams.associate { it.first to it.second }
        val endParams = endParams.associate { it.first to it.second }

        var observable = Observable.just(eventName)
            .doOnNext {
                val result = FlurryAgent.logEvent(it!!, startParams, isTimedEvent)

                if (result != FlurryEventRecordStatus.kFlurryEventRecorded) {
                    throw Throwable(result.name)
                }
            }

        if (isTimedEvent) {
            observable = observable.delay(delayTime, TimeUnit.SECONDS)
                .doOnNext {
                    FlurryAgent.endTimedEvent(it!!, endParams)
                }
        }

        displayToast.setValue("Logging event: $eventName, start params: ${startParams.size}, end params: ${endParams.size}")

        val disposable = observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    // onNext
                },
                {
                    // onError
                    displayToast.setValue("Fail to log event: ${it.message}")
                },
                {
                    // onComplete
                    displayToast.setValue("Log success")
                }
            )

        compositeDisposable.add(disposable)
    }

    fun validateParam(key: String, value: String) {
        if (key.isEmpty() || value.isEmpty()) {
            if (key.isEmpty()) {
                keyErrorMessage.setValue("Key could not be empty")
            }

            if (value.isEmpty()) {
                valueErrorMessage.setValue("Value could not be empty")
            }

            return
        }

        val isTimedEvent = this.isTimedEvent.value!!
        val paramCount = startParams.size + (if (isTimedEvent) endParams.size else 0)

        this.paramCount.setValue(paramCount)
    }


    override fun onCleared() {
        compositeDisposable.clear()
    }
}