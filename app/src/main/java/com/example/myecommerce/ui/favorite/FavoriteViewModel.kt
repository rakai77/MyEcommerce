package com.example.myecommerce.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.model.UserPreference
import com.example.myecommerce.data.repository.product.ProductRepository
import com.example.myecommerce.data.response.ListFavoriteProductResponse
import com.example.myecommerce.data.response.ListProductResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val productRepository: ProductRepository, private val pref: UserPreference) :
    ViewModel() {

    private val _state = MutableLiveData<Result<ListFavoriteProductResponse>>()
    val state: LiveData<Result<ListFavoriteProductResponse>> = _state

    private var searchJob: Job? = null

    init {
        onSearch("")
    }

    fun onSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            if (query.isEmpty()) {
                getFavoriteList("", pref.getUserId().first())
            } else {
                delay(2000)
                getFavoriteList(query, pref.getUserId().first())
            }
        }
    }

    fun getFavoriteList(query: String?, userID: Int) {
        productRepository.getListFavoriteProduct(query, userID).onEach { result ->
            when (result) {
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
