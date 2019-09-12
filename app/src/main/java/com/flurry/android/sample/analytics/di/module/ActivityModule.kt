package com.flurry.android.sample.analytics.di.module

import com.flurry.android.sample.analytics.MainActivity
import com.flurry.android.sample.analytics.di.scope.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [FragmentModule::class])
    @ActivityScope
    abstract fun contributeMainActivity(): MainActivity
}