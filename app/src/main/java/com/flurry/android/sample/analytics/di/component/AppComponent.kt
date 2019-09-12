package com.flurry.android.sample.analytics.di.component

import android.app.Application
import com.flurry.android.sample.analytics.AnalyticsSampleApplication
import com.flurry.android.sample.analytics.di.module.ActivityModule
import com.flurry.android.sample.analytics.di.module.AppModule
import com.flurry.android.sample.analytics.di.module.FragmentModule
import com.flurry.android.sample.analytics.di.module.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ActivityModule::class, FragmentModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(analyticsSampleApplication: AnalyticsSampleApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}