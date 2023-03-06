package com.example.myecommerce.data.response

import com.google.gson.annotations.SerializedName

data class UpdateStockResponse(

	@field:SerializedName("success")
	val success: SuccessUpdateStock
)

data class SuccessUpdateStock(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)
