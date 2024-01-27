package com.jinin4.journalog

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.jinin4.journalog.AppLifecycleObserver
import dagger.hilt.android.HiltAndroidApp

//반정현 - 24.01.22
@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle
            .addObserver(AppLifecycleObserver(applicationContext))
    }

}