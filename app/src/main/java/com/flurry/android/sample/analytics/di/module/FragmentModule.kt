package com.flurry.android.sample.analytics.di.module

import com.flurry.android.sample.analytics.di.scope.FragmentScope
import com.flurry.android.sample.analytics.ui.fragment.CrashFragment
import com.flurry.android.sample.analytics.ui.fragment.EventFragment
import com.flurry.android.sample.analytics.ui.fragment.OtherFeaturesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): EventFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeDashboardFragment(): CrashFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeNotificationsFragment(): OtherFeaturesFragment
}