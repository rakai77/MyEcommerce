package com.example.myecommerce.ui.buy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.model.UpdateRate
import com.example.myecommerce.data.repository.product.ProductRepository
import com.example.myecommerce.data.response.UpdateRateResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyViewModel @Inject constructor(private val productRepository: ProductRepository) : ViewModel() {

    private val _state = MutableLiveData<Result<UpdateRateResponse>>()
    val state: LiveData<Result<UpdateRateResponse>> = _state

    fun updateRate(id: Int, updateRate: UpdateRate) {
        productRepository.updateRate(id, updateRate).onEach { result ->
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

    fun deleteProductByIdFromTrolley(id: Int?) {
        viewModelScope.launch {
            productRepository.deleteProductByIdFromTrolley(id)
        }
    }
}