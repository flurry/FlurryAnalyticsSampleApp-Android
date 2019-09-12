package com.flurry.android.sample.analytics.util

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean


class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }

        super.observe(owner, Observer {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }

    override fun setValue(t: T) {
        mPending.set(true)
        super.setValue(t)
    }

    fun cleanAndObserve(owner: LifecycleOwner, observer: Observer<in T>) {
        removeObservers(owner)
        observe(owner, observer)
    }

    companion object {
        private const val TAG = "SingleLiveEvent"
    }
}