package com.flurry.android.sample.analytics.di.module

import android.content.Context
import com.flurry.android.sample.analytics.AnalyticsSampleApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideApplicationContext(application: AnalyticsSampleApplication): Context = application.applicationContext
}