package com.example.myecommerce.data.api

import android.util.Log
import com.example.myecommerce.data.api.service.ApiRefreshToken
import com.example.myecommerce.data.model.UserPreference
import com.example.myecommerce.data.response.RefreshTokenResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class AuthAuthentication @Inject constructor(private val pref: UserPreference) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {

            val auth = pref.getSaveAuthToken().first()

            val newToken = getNewToken(
                auth.userId,
                auth.refreshToken,
                auth.accessToken,
            )
            println("token $newToken")

            if (!newToken.isSuccessful || newToken.body() == null || newToken.code() == 401) {
                pref.logout() // Clear Preferences
            }

            runBlocking {
                newToken.body()?.let {
                    pref.saveRefreshToken(it.success.accessToken)
                    pref.saveRefreshToken(it.success.refreshToken)
                    Log.d("Check access token", "token = ${it.success.accessToken} " )
                    Log.d("Check refresh token", "token = ${it.success.refreshToken} " )

                    response.request.newBuilder()
                        .header("Authorization", it.success.accessToken) // Save New Token To Header
                        .build()
                }
            }
        }
    }

    private suspend fun getNewToken(
        userId: Int?,
        accessToken: String?,
        refreshToken: String?
    ): retrofit2.Response<RefreshTokenResponse> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://portlan.id/training_android/public/api/ecommerce/")
//            .baseUrl("http://172.17.20.201/training_android/public/api/ecommerce/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val service = retrofit.create(ApiRefreshToken::class.java)
        return service.refreshToken(userId, accessToken, refreshToken)
    }
}