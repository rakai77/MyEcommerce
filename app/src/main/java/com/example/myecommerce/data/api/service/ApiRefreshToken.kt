package com.example.myecommerce.data.api.service

import com.example.myecommerce.data.response.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiRefreshToken {

    @FormUrlEncoded
    @Headers("apikey: TuIBt77u7tZHi8n7WqUC")
    @POST("refresh-token")
    suspend fun refreshToken(
        @Field("id_user") id: Int?,
        @Field("access_token") accessToken: String?,
        @Field("refresh_token") refreshToken: String?
    ): Response<RefreshTokenResponse>

}