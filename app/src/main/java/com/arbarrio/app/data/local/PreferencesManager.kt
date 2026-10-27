package com.arbarrio.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
        val SAVED_EMAIL_KEY = stringPreferencesKey("saved_email")
        val BIOMETRIC_ENABLED_KEY = booleanPreferencesKey("biometric_enabled")
        val LANGUAGE_KEY = stringPreferencesKey("app_language")
    }

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "es"
    }

    val isRememberMeEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[REMEMBER_ME_KEY] ?: false
    }

    val savedEmail: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SAVED_EMAIL_KEY] ?: ""
    }

    val isBiometricEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[BIOMETRIC_ENABLED_KEY] ?: false
    }

    suspend fun saveRememberMeSettings(enabled: Boolean, email: String) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_ME_KEY] = enabled
            if (enabled) {
                preferences[SAVED_EMAIL_KEY] = email
            } else {
                preferences.remove(SAVED_EMAIL_KEY)
            }
        }
    }

    suspend fun saveBiometricSetting(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED_KEY] = enabled
        }
    }

    suspend fun saveLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }
}
