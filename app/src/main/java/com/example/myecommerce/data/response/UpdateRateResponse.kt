package com.example.myecommerce.data.response

import com.google.gson.annotations.SerializedName

data class UpdateRateResponse(

    @field:SerializedName("success")
    val success: SuccessUpdateRate
)

data class SuccessUpdateRate(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Int
)
