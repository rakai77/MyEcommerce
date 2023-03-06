package com.example.myecommerce.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myecommerce.data.repository.auth.AuthRepository
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.model.UserPreference
import com.example.myecommerce.data.response.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val pref: UserPreference
) : ViewModel() {

    private val _state = MutableLiveData<Result<LoginResponse>>()
    val state: LiveData<Result<LoginResponse>> = _state

    val getToken = pref.getToken()

    fun login(email: String, password: String, tokenFCM: String) {
        authRepository.login(email, password, tokenFCM).onEach { result ->
            when(result) {
                is Result.Loading -> {
                    _state.value = Result.Loading
                }
                is Result.Success -> {
                    result.data.let {
                        _state.value = Result.Success(it)
                        pref.saveAuthToken(
                            it.success.dataUser.id,
                            it.success.accessToken,
                            it.success.refreshToken
                        )
                        pref.saveUserEmail(it.success.dataUser.email)
                        pref.savePathImage(it.success.dataUser.path)
                        pref.saveUserName(it.success.dataUser.name)
                        pref.saveUserID(it.success.dataUser.id)

                        Log.d("get pref", "$it")
                    }
                }
                is Result.Error -> {
                    _state.value = Result.Error(result.message, result.errorCode, result.errorBody)
                }
            }
        }.launchIn(viewModelScope)
    }
}