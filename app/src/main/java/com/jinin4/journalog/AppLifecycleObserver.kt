package com.jinin4.journalog

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.jinin4.journalog.applock.ScreenLockActivity
import com.jinin4.journalog.utils.PasswordUtils
import kotlinx.coroutines.runBlocking
// 반정현 작성 - 24.01.28
class AppLifecycleObserver(
    private val context: Context
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onForeground() {
        runBlocking {
            val password = PasswordUtils.getPasswordFromDataStore(context)
            if (password.isNotEmpty()) ScreenLockActivity.start(context)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onBackground() {
        //background
    }
}