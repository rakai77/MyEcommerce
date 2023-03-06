package com.example.myecommerce.data.response

import com.google.gson.annotations.SerializedName

data class DetailProductResponse(

	@field:SerializedName("success")
	val success: SuccessDetailProduct
)

data class ImageProductItem(

	@field:SerializedName("image_product")
	val imageProduct: String,

	@field:SerializedName("title_product")
	val titleProduct: String
)

data class DataItemProduct(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("name_product")
	val nameProduct: String,

	@field:SerializedName("weight")
	val weight: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("harga")
	val harga: String,

	@field:SerializedName("size")
	val size: String,

	@field:SerializedName("rate")
	val rate: Int,

	@field:SerializedName("image_product")
	val imageProduct: List<ImageProductItem>,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("stock")
	val stock: Int,

	@field:SerializedName("desc")
	val desc: String,

	@field:SerializedName("isFavorite")
	val isFavorite: Boolean
)

data class SuccessDetailProduct(

	@field:SerializedName("data")
	val data: DataItemProduct,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)
