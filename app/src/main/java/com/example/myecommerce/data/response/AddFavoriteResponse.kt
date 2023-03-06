package com.example.myecommerce.data.response

import com.google.gson.annotations.SerializedName

data class AddFavoriteResponse(

	@field:SerializedName("success")
	val success: SuccessAddFavorite
)

data class SuccessAddFavorite(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)
