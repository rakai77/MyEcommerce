package com.example.myecommerce.utils

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constanta {
    val TOKEN = stringPreferencesKey("token")
    val USER_ID = intPreferencesKey("user_id")
    val NAME = stringPreferencesKey("name")
    val EMAIL = stringPreferencesKey("email")
    val PATH = stringPreferencesKey("path")
    val LANGUAGE_PREF = intPreferencesKey("language")
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
}