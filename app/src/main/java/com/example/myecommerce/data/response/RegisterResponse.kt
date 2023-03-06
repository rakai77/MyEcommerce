package com.example.myecommerce.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("success")
	val success: SuccessRegister
)

data class SuccessRegister(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)
