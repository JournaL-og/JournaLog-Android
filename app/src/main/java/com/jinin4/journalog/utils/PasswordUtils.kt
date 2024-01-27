package com.jinin4.journalog.utils

import android.content.Context
import android.widget.Toast
import com.jinin4.journalog.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

object PasswordUtils {
    suspend fun getPasswordFromDataStore(context: Context): String {
        return withContext(Dispatchers.IO) {
            val preferences = context.dataStore?.data?.first()
            preferences?.password ?: ""
        }
    }

    suspend fun savePasswordToDataStore(context: Context, newPassword: String) {
        context.dataStore?.updateData { preferences ->
            preferences.toBuilder().setPassword(newPassword).build()
        }
    }

    suspend fun clearPasswordInDataStore(context: Context) {
        context.dataStore?.updateData { preferences ->
            preferences.toBuilder().setPassword("").build()
        }
    }

    suspend fun checkPassword(context: Context, enteredPassword: String): Boolean {
        val storedPassword = getPasswordFromDataStore(context)
        return enteredPassword == storedPassword
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun isValidPassword(password: String): Boolean {
        return password.matches(Regex("\\d{4}"))
    }
}