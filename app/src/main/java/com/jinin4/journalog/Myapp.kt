package com.jinin4.journalog

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp

//반정현 - 24.01.22
@HiltAndroidApp
class MyApp : Application() {
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
}