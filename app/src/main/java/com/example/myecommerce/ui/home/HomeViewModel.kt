package com.example.myecommerce.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.repository.product.ProductRepository
import com.example.myecommerce.data.response.ListProductResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val productRepository: ProductRepository): ViewModel() {

    private val _state = MutableLiveData<Result<ListProductResponse>>()
    val state: LiveData<Result<ListProductResponse>> = _state

    init {
        getListProduct("")
    }

    fun getListProduct(query: String?) {
        productRepository.getListProduct(query).onEach { result ->
            when(result){
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