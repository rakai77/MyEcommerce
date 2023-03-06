package com.example.myecommerce.data.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.myecommerce.utils.Constanta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "application")

class UserPreference constructor(
    private val dataStore: DataStore<Preferences>
) {

    fun getUserId() : Flow<Int> {
        return dataStore.data.map { preference ->
            preference[Constanta.USER_ID] ?: 0
        }
    }

    suspend fun saveUserID(id: Int) {
        dataStore.edit { preference ->
            preference[Constanta.USER_ID] = id
        }
    }

    fun getUserName() : Flow<String> {
        return dataStore.data.map { preference ->
            preference[Constanta.NAME] ?: ""
        }
    }

    suspend fun saveUserName(name: String) {
        dataStore.edit { preference ->
            preference[Constanta.NAME] = name
        }
    }

    fun getUserEmail(): Flow<String> {
        return dataStore.data.map { preference ->
            preference[Constanta.EMAIL] ?: ""
        }
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preference ->
            preference[Constanta.EMAIL] = email
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preference ->
            preference[Constanta.TOKEN] ?: ""
        }
    }

    suspend fun setToken(message: String) {
        dataStore.edit { preference ->
            preference[Constanta.TOKEN] = message
        }
    }

    fun getPathImage() : Flow<String> {
       return dataStore.data.map { preference ->
           preference[Constanta.PATH] ?: ""
       }
    }

    suspend fun savePathImage(path: String) {
        dataStore.edit { preference ->
            preference[Constanta.PATH] = path
        }
    }

    fun getLanguage() : Flow<Int> {
        return dataStore.data.map { preference ->
            preference[Constanta.LANGUAGE_PREF] ?: 0
        }
    }

    suspend fun saveLanguage(position: Int) {
        dataStore.edit { preference ->
            preference[Constanta.LANGUAGE_PREF]  = position
        }
    }

    suspend fun logout() {
        dataStore.edit { preference ->
            preference.clear()
        }
    }

    suspend fun saveUpdateImage(path: String) {
        dataStore.edit { preference ->
            preference[Constanta.PATH] = path
        }
    }

    fun getRefreshToken() : Flow<String> {
        return dataStore.data.map { preference ->
            preference[Constanta.REFRESH_TOKEN] ?: ""
        }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit { preference->
            preference[Constanta.REFRESH_TOKEN] = refreshToken
        }
    }

    fun getSaveAuthToken() : Flow<RefreshTokenModel> {
        return dataStore.data.map { preference ->
            RefreshTokenModel(
                preference[Constanta.USER_ID] ?: 0,
                preference[Constanta.TOKEN] ?: "",
                preference[Constanta.ACCESS_TOKEN] ?: ""
            )
        }
    }

    suspend fun saveAuthToken(id: Int, token: String, refreshToken: String) {
        dataStore.edit { preference ->
            preference[Constanta.USER_ID] = id
            preference[Constanta.TOKEN] = token
            preference[Constanta.REFRESH_TOKEN] = refreshToken
        }
    }

}
