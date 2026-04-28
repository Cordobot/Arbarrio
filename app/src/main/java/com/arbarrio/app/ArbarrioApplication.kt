package com.arbarrio.app

import android.app.Application
import com.arbarrio.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ArbarrioApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@ArbarrioApplication)
            modules(appModule)
        }
    }
}
