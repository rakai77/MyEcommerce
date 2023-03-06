package com.example.myecommerce.data.api

import com.example.myecommerce.data.model.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(private val pref: UserPreference) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            pref.getToken().first()
        }
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("apikey", "TuIBt77u7tZHi8n7WqUC")
            .addHeader("Authorization", token)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }

}