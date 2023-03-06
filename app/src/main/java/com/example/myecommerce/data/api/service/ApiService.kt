package com.example.myecommerce.data.api.service

import com.example.myecommerce.data.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("authentication")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("token_fcm") tokenFCM: String
    ): LoginResponse

    @Multipart
    @POST("registration")
    suspend fun register(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("gender") gender: Int,
        @Part image: MultipartBody.Part
    ): RegisterResponse

    @FormUrlEncoded
    @PUT("change-password/{id}")
    suspend fun changePassword(
        @Path("id") id: Int,
        @Field("password") password: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_password") confirmPassword: String
    ) : ChangePasswordResponse

    @Multipart
    @POST("change-image")
    suspend fun changeImage(
        @Part ("id") id: Int,
        @Part image: MultipartBody.Part
    ) : ChangeImageResponse
}