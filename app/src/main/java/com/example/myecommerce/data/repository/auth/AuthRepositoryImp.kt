package com.example.myecommerce.data.repository.auth

import com.example.myecommerce.data.Result
import com.example.myecommerce.data.api.service.ApiService
import com.example.myecommerce.data.response.ChangeImageResponse
import com.example.myecommerce.data.response.ChangePasswordResponse
import com.example.myecommerce.data.response.LoginResponse
import com.example.myecommerce.data.response.RegisterResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(private val apiService: ApiService) : AuthRepository {
    override fun login(email: String, password: String, tokenFCM: String): Flow<Result<LoginResponse>> = flow {
        emit(Result.Loading)
        try {
            val result = apiService.login(email, password, tokenFCM)
            emit(Result.Success(result))
        } catch (t: Throwable) {
            if (t is HttpException) {
                when (t.code()) {
                    400 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    401 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    404 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    500 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    else -> emit(Result.Error(null, null, null))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e.localizedMessage, null, null))
        }
    }

    override fun register(
        image: MultipartBody.Part,
        email: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        gender: Int
    ): Flow<Result<RegisterResponse>> = flow {
        emit(Result.Loading)
        try {
            val result = apiService.register(name, email, password, phone, gender, image)
            emit(Result.Success(result))
        } catch (t: Throwable) {
            if (t is HttpException) {
                when (t.code()) {
                    400 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    401 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    404 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    500 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    else -> emit(Result.Error(null, null, null))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e.localizedMessage, null, null))
        }
    }

    override fun changePassword(
        id: Int,
        password: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<Result<ChangePasswordResponse>> = flow {
        emit(Result.Loading)
        try {
            val result = apiService.changePassword(id, password, newPassword, confirmPassword)
            emit(Result.Success(result))
        } catch (t: Throwable) {
            if (t is HttpException) {
                when (t.code()) {
                    400 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    401 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    404 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    500 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    else -> emit(Result.Error(null, null, null))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e.localizedMessage, null, null))
        }
    }

    override fun changeImage(
        id: Int,
        image: MultipartBody.Part
    ): Flow<Result<ChangeImageResponse>> = flow {
        emit(Result.Loading)
        try {
            val result = apiService.changeImage(id, image)
            emit(Result.Success(result))
        } catch (t: Throwable) {
            if (t is HttpException) {
                when (t.code()) {
                    400 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    401 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    404 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    500 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    else -> emit(Result.Error(null, null, null))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e.localizedMessage, null, null))
        }
    }
}