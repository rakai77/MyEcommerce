package com.example.myecommerce.ui.password

import androidx.lifecycle.*
import com.example.myecommerce.data.repository.auth.AuthRepository
import com.example.myecommerce.data.model.UserPreference
import com.example.myecommerce.data.response.ChangePasswordResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import com.example.myecommerce.data.Result
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val pref: UserPreference,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableLiveData<Result<ChangePasswordResponse>>()
    val state: LiveData<Result<ChangePasswordResponse>> = _state

    val getAuth = pref.getToken()
    val getUserID = pref.getUserId()


    fun changePassword(
        id: Int,
        password: String,
        newPassword: String,
        confirmPassword: String
    ) {
        authRepository.changePassword(
            id, password, newPassword, confirmPassword
        ).onEach { result ->
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