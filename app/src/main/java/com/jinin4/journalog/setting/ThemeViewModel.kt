package com.jinin4.journalog.setting

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.jinin4.journalog.Preference
import com.jinin4.journalog.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

//반정현 작성 - 24.01.26
@HiltViewModel
class ThemeViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    val theme: LiveData<Preference.Theme> = context.dataStore.data.map { it.theme }.asLiveData()

    suspend fun changeTheme(theme: Preference.Theme) {
        context.dataStore.updateData { preferences ->
            preferences.toBuilder().setTheme(theme).build()
        }
    }
}

