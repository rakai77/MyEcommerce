package com.example.myecommerce.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myecommerce.data.repository.auth.AuthRepository
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.response.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableLiveData<Result<RegisterResponse>>()
    val state: LiveData<Result<RegisterResponse>> = _state

    fun register(
        image: MultipartBody.Part,
        email: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        gender: Int
    ) {
        authRepository.register(image, email, password, name, phone, gender).onEach { result ->
            when(result) {
                is Result.Loading -> {
                    _state.value = Result.Loading

                }
                is Result.Success -> {
                    result.data.let {
                        _state.value = Result.Success(it)
                    }
                }
                is Result.Error -> {
                    _state.value = Result.Error(result.message, result.errorCode, result.errorBody)
                }
            }
        }.launchIn(viewModelScope)
    }
}