package com.example.myecommerce.ui.trolley

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.local.room.ProductEntity
import com.example.myecommerce.data.model.DataStock
import com.example.myecommerce.data.model.DataStockItem
import com.example.myecommerce.data.repository.product.ProductRepository
import com.example.myecommerce.data.response.UpdateStockResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrolleyViewModel @Inject constructor(private val productRepository: ProductRepository) :
    ViewModel() {

    private val _updateStockState = MutableLiveData<Result<UpdateStockResponse>>()
    val updateStockState: LiveData<Result<UpdateStockResponse>> = _updateStockState

    fun updateStock(userID: String, data: List<DataStockItem>) {
        productRepository.updateStock(DataStock(userID, data)).onEach { result ->
            when (result) {
                is Result.Loading -> {
                    _updateStockState.value = Result.Loading
                }
                is Result.Success -> {
                    result.data.let {
                        _updateStockState.value = Result.Success(it)
                    }
                }
                is Result.Error -> {
                    _updateStockState.value =
                        Result.Error(result.message, result.errorCode, result.errorBody)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getAllProduct(): Flow<List<ProductEntity>> {
        return productRepository.getAllProduct()
    }

    fun getAllProductIsChecked(): Flow<List<ProductEntity>> {
        return productRepository.getAllCheckedProduct()
    }

    fun getProductById(id: Int?): Flow<List<ProductEntity>> {
        return productRepository.getProductById(id)
    }

    fun updateProductData(quantity: Int?, itemTotalPrice: Int?, id: Int?) {
        viewModelScope.launch {
            productRepository.updateProductData(quantity, itemTotalPrice, id)
        }
    }

    fun updateProductIsCheckedAll(isChecked: Boolean) {
        viewModelScope.launch {
            productRepository.updateProductIsCheckedAll(isChecked)
        }
    }

    fun updateProductIsCheckedById(isChecked: Boolean, id: Int?) {
        viewModelScope.launch {
            productRepository.updateProductIsCheckedById(isChecked, id)
        }
    }

    fun deleteProductByIdFromTrolley(id: Int?) {
        viewModelScope.launch {
            productRepository.deleteProductByIdFromTrolley(id)
        }
    }
}