package com.example.myecommerce.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myecommerce.data.repository.product.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.local.room.ProductEntity
import com.example.myecommerce.data.response.AddFavoriteResponse
import com.example.myecommerce.data.response.DetailProductResponse
import com.example.myecommerce.data.response.RemoveFavoriteResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class DetailProductViewModel @Inject constructor(private val productRepository: ProductRepository) : ViewModel(){

    private val _detail = MutableLiveData<Result<DetailProductResponse>>()
    val detail: LiveData<Result<DetailProductResponse>> = _detail

    private val _addFavorite = MutableLiveData<Result<AddFavoriteResponse>>()
    val addFavorite: LiveData<Result<AddFavoriteResponse>> = _addFavorite

    private val _removeFavorite = MutableLiveData<Result<RemoveFavoriteResponse>>()
    val removeFavorite: LiveData<Result<RemoveFavoriteResponse>> = _removeFavorite

    fun onRefresh(productId: Int, userId: Int) {
        getDetailProduct(productId, userId)
    }

    fun getDetailProduct(productID: Int, userID: Int) {
        productRepository.getDetailProduct(productID, userID).onEach { result ->
            when (result) {
                is Result.Loading -> {
                    _detail.value = Result.Loading
                }
                is Result.Success -> {
                    result.data.let {
                        _detail.value = Result.Success(it)
                    }
                }
                is Result.Error -> {
                    _detail.value = Result.Error(result.message, result.errorCode, result.errorBody)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun addFavoriteProduct(productID: Int, userID: Int) {
        productRepository.addFavoriteProduct(productID, userID).onEach { result ->
            when(result) {
                is Result.Loading -> {
                    _addFavorite.value = Result.Loading
                }
                is Result.Success -> {
                    result.data.let {
                        _addFavorite.value = Result.Success(it)
                    }
                }
                is Result.Error -> {
                    _addFavorite.value = Result.Error(result.message, result.errorCode, result.errorBody)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun removeFavoriteProduct(productID: Int, userID: Int) {
        productRepository.removeFavoriteProduct(productID, userID).onEach { result ->
            when(result) {
                is Result.Loading -> {
                    _removeFavorite.value = Result.Loading
                }
                is Result.Success -> {
                    result.data.let {
                        _removeFavorite.value = Result.Success(it)
                    }
                }
                is Result.Error -> {
                    _removeFavorite.value = Result.Error(result.message, result.errorCode, result.errorBody)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun insertTrolley(productEntity: ProductEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            productRepository.addProductToTrolley(productEntity)
        }
    }

}