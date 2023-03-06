package com.example.myecommerce.data.response

import com.google.gson.annotations.SerializedName

data class RemoveFavoriteResponse(

    @field:SerializedName("success")
    val success: SuccessRemoveFavorite
)

data class SuccessRemoveFavorite(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Int
)
