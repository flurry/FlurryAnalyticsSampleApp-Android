package com.flurry.android.sample.analytics

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.flurry.android.FlurryAgent
import com.flurry.android.sample.analytics.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class AnalyticsSampleApplication : Application(), HasActivityInjector {
    @Inject
    lateinit var dispatchingActivityAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        context = applicationContext

        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)

        FlurryAgent.Builder()
            .withLogLevel(Log.VERBOSE)
            .withLogEnabled(true)
            .build(this, getString(R.string.FLURRY_APIKEY))
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityAndroidInjector

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}