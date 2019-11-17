package com.grafsemmel.translationfun.application

import android.app.Application
import com.grafsemmel.translationfun.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            //inject Android context
            androidContext(this@DiApplication)
            // use Android logger - Level.INFO by default
            androidLogger()
            // use properties from assets/koin.properties
//            androidFileProperties()
            // use modules
            modules(appModule)
        }
    }
}