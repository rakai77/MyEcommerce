package com.example.myecommerce.data.response

import com.google.gson.annotations.SerializedName

data class RegisterErrorResponse(

	@field:SerializedName("error")
	val error: Error
)

data class Error(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)
