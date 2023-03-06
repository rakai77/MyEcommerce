package com.example.myecommerce.data.response

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(

    @field:SerializedName("success")
    val success: RefreshTokenSuccess
)

data class RefreshTokenSuccess(

    @field:SerializedName("access_token")
    val accessToken: String,

    @field:SerializedName("refresh_token")
    val refreshToken: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Int
)
