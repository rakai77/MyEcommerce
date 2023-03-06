package com.example.myecommerce.data.response

import com.google.gson.annotations.SerializedName

class ChangePasswordResponse (

    @field:SerializedName("success")
    val success: ChangePasswordSuccess
)

data class ChangePasswordSuccess(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Int
)
