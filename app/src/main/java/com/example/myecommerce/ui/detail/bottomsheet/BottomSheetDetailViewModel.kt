package com.example.myecommerce.ui.detail.bottomsheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myecommerce.data.model.DataStock
import com.example.myecommerce.data.model.DataStockItem
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.repository.product.ProductRepository
import com.example.myecommerce.data.response.UpdateStockResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BottomSheetDetailViewModel @Inject constructor(private val productRepository: ProductRepository) : ViewModel() {

    private var _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    private var _price = MutableLiveData<Int>()
    val price: LiveData<Int> = _price

    private val _updateStockState = MutableLiveData<Result<UpdateStockResponse>>()
    val updateStockState: LiveData<Result<UpdateStockResponse>> = _updateStockState

    private var initPrice: Int? = 0

    init {
        _quantity.value = 1
    }

    fun updateStock(userID: String, productID: String, stock: Int) {
        productRepository.updateStock(DataStock(userID, listOf(DataStockItem(productID, stock)))).onEach { result ->
            when(result) {
                is Result.Loading -> {
                    _updateStockState.value = Result.Loading
                }
                is Result.Success -> {
                    result.data.let {
                        _updateStockState.value = Result.Success(it)
                    }
                }
                is Result.Error -> {
                    _updateStockState.value = Result.Error(result.message, result.errorCode, result.errorBody)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun increaseQuantity(stock: Int?) {
        if (_quantity.value!! < stock!!) {
            _quantity.value = _quantity.value?.plus(1)
            _price.value = initPrice?.times(_quantity.value!!.toInt())
        }
    }

    fun decreaseQuantity() {
        if (quantity.value == 1) {
            _quantity.value = 1
            _price.value = initPrice!!.toInt()
        } else {
            _quantity.value = _quantity.value?.minus(1)
            _price.value = initPrice?.times(_quantity.value!!.toInt())
        }
    }

    fun setPrice(productPrice: Int) {
        initPrice = productPrice
        _price.value = productPrice
    }
}