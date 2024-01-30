package com.jinin4.journalog

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp

//반정현 작성 - 24.01.22
//반정현 수정 - 24.01.28
@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle
            .addObserver(AppLifecycleObserver(applicationContext))
    }

}