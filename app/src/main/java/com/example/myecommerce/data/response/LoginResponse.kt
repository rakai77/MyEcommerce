package com.example.myecommerce.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("success")
	val success: Success
)

data class DataUser(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("path")
	val path: String,

	@field:SerializedName("gender")
	val gender: Int,

	@field:SerializedName("phone")
	val phone: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("email")
	val email: String
)

data class Success(

	@field:SerializedName("access_token")
	val accessToken: String,

	@field:SerializedName("refresh_token")
	val refreshToken: String,

	@field:SerializedName("data_user")
	val dataUser: DataUser,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)
