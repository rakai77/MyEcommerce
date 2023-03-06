package com.example.myecommerce.ui.profile

import androidx.lifecycle.*
import com.example.myecommerce.data.repository.auth.AuthRepository
import com.example.myecommerce.data.model.UserPreference
import com.example.myecommerce.data.response.ChangeImageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import com.example.myecommerce.data.Result
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val pref: UserPreference,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableLiveData<Result<ChangeImageResponse>>()
    val state: LiveData<Result<ChangeImageResponse>> = _state

    val getNameUser = pref.getUserName()
    val getEmailUser = pref.getUserEmail()
    val getUserID = pref.getUserId()
    val getImagePath = pref.getPathImage()
    val getLanguage = pref.getLanguage()

    fun saveImagePath(image: String) {
        viewModelScope.launch {
            pref.savePathImage(image)
        }
    }

    fun saveLanguage(position: Int) {
        viewModelScope.launch {
            pref.saveLanguage(position)
        }
    }

    fun changeImage(id: Int, image: MultipartBody.Part) {
        authRepository.changeImage(id, image).onEach { result ->
            when (result) {
                is Result.Loading -> {
                    _state.value = Result.Loading
                }
                is Result.Success -> {
                    result.data?.let {
                        _state.value = Result.Success(it)
                    }
                }
                is Result.Error -> {
                    _state.value =
                        Result.Error(result.message, result.errorCode, result.errorBody)
                }
            }
        }.launchIn(viewModelScope)
    }
    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}