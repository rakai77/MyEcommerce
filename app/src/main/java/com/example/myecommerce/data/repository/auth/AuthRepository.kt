package com.example.myecommerce.data.repository.auth

import com.example.myecommerce.data.Result
import com.example.myecommerce.data.response.ChangeImageResponse
import com.example.myecommerce.data.response.ChangePasswordResponse
import kotlinx.coroutines.flow.Flow
import com.example.myecommerce.data.response.LoginResponse
import com.example.myecommerce.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AuthRepository {

    fun login(email: String, password: String, tokenFCM: String): Flow<Result<LoginResponse>>

    fun register(
        image: MultipartBody.Part,
        email: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        gender: Int
    ) : Flow<Result<RegisterResponse>>

    fun changePassword(id: Int, password: String, newPassword: String, confirmPassword: String): Flow<Result<ChangePasswordResponse>>

    fun changeImage(id: Int, image: MultipartBody.Part): Flow<Result<ChangeImageResponse>>
}