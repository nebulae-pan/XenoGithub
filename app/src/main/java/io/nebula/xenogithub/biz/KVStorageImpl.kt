package io.nebula.xenogithub.biz

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Created by nebula on 2025/3/7
 */
object KVStorageImpl {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private val ACCESS_CODE = stringPreferencesKey("access_code")

    // todo simplify
    suspend fun saveAccessToken(context: Context, value: String) {
        context.dataStore.edit { settings ->
            settings[ACCESS_TOKEN] = value
        }
    }

    suspend fun saveAccessCode(context: Context, value: String) {
        context.dataStore.edit { settings ->
            settings[ACCESS_CODE] = value
        }
    }

    fun getAccessToken(context: Context): String = runBlocking {
        context.dataStore.data.map { it[ACCESS_TOKEN] }.first() ?: ""
    }

    fun getAccessCode(context: Context): String = runBlocking {
        context.dataStore.data.map { it[ACCESS_CODE] }.first() ?: ""
    }
}