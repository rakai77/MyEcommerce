package com.example.myecommerce.data.model

data class RefreshTokenModel(
    val userId: Int,
    val accessToken: String,
    val refreshToken: String
)