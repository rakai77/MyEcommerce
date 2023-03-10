package com.example.myecommerce.data.api

import android.content.Context
import android.content.Intent
import com.example.myecommerce.data.model.UserPreference
import com.example.myecommerce.ui.login.LoginActivity
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthExpiredAuthentication @Inject constructor(
    private val pref: UserPreference,
    private val context: Context
) : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == 401) {
            runBlocking {
                println("masuk expired token clear preference")
                pref.logout() // Clear Preferences
            }
            val intent = Intent(context, LoginActivity::class.java) // Back to Login Screen
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            return response
        }
        return response
    }
}