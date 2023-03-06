package com.example.myecommerce.data.response

import com.google.gson.annotations.SerializedName

data class ChangeImageResponse(

    @SerializedName("success")
    val success: SuccessImage
)

data class SuccessImage(
    @SerializedName("path")
    val path: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int
)

