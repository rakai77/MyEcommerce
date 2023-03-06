package com.example.myecommerce.data.response

import com.google.gson.annotations.SerializedName

data class ListFavoriteProductResponse(

	@field:SerializedName("success")
	val success: SuccessFavoriteProduct
)

data class DataItemFavorite(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("name_product")
	val nameProduct: String,

	@field:SerializedName("harga")
	val harga: String,

	@field:SerializedName("size")
	val size: String,

	@field:SerializedName("rate")
	val rate: Int,

	@field:SerializedName("weight")
	val weight: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("stock")
	val stock: Int,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("desc")
	val desc: String
)

data class SuccessFavoriteProduct(

	@field:SerializedName("data")
	val data: List<DataItemFavorite>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)
