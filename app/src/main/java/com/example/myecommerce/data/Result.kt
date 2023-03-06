package com.example.myecommerce.data

import okhttp3.ResponseBody

sealed class Result <out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String?, val errorCode: Int?, val errorBody: ResponseBody?) : Result<Nothing>()
    object Loading : Result<Nothing>()
}