package com.augieafr.storyapp.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private object PreferenceKeys {
        val TOKEN_KEY = stringPreferencesKey("user_token")
    }

    fun getUserToken(): Flow<String> {
        return dataStore.data.map {
            it[PreferenceKeys.TOKEN_KEY] ?: ""
        }
    }

    suspend fun setUserToken(token: String) {
        dataStore.edit {
            val currentToken = it[PreferenceKeys.TOKEN_KEY]
            if (token == currentToken) return@edit
            it[PreferenceKeys.TOKEN_KEY] = token
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}