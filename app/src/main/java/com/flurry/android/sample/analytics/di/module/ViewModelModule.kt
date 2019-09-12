package com.flurry.android.sample.analytics.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flurry.android.sample.analytics.di.key.ViewModelKey
import com.flurry.android.sample.analytics.ui.viewmodel.AnalyticsSampleViewModelFactory
import com.flurry.android.sample.analytics.ui.viewmodel.CrashViewModel
import com.flurry.android.sample.analytics.ui.viewmodel.EventViewModel
import com.flurry.android.sample.analytics.ui.viewmodel.OtherFeaturesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: AnalyticsSampleViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(EventViewModel::class)
    abstract fun bindHomeViewModel(eventViewModel: EventViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OtherFeaturesViewModel::class)
    abstract fun bindNotificationsViewModel(otherFeaturesViewModel: OtherFeaturesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CrashViewModel::class)
    abstract fun bindDashboardViewModel(crashViewModel: CrashViewModel): ViewModel
}