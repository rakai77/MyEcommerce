package com.example.myecommerce.data.repository.product

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import com.example.myecommerce.data.model.DataStock
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.local.room.ProductEntity
import com.example.myecommerce.data.model.UpdateRate
import com.example.myecommerce.data.response.*

interface ProductRepository {

    fun getListProduct(product: String?) : Flow<Result<ListProductResponse>>

    fun getListProductPaging(query: String) : PagingSource<Int, DataItem>

    fun getListFavoriteProduct(product: String?, userID: Int) : Flow<Result<ListFavoriteProductResponse>>

    fun getDetailProduct(productID: Int, userID: Int) : Flow<Result<DetailProductResponse>>

    fun addFavoriteProduct(productID: Int, userID: Int) : Flow<Result<AddFavoriteResponse>>

    fun removeFavoriteProduct(productID: Int, userID: Int) : Flow<Result<RemoveFavoriteResponse>>

    fun updateStock(updateStock: DataStock) : Flow<Result<UpdateStockResponse>>

    fun updateRate(id: Int, updateRate: UpdateRate) : Flow<Result<UpdateRateResponse>>

    fun getAllProduct(): Flow<List<ProductEntity>>
    fun getAllCheckedProduct(): Flow<List<ProductEntity>>
    fun getProductById(id: Int?): Flow<List<ProductEntity>>
    suspend fun addProductToTrolley(trolley: ProductEntity)
    suspend fun updateProductData(quantity: Int?, itemTotalPrice: Int?, id: Int?)
    suspend fun updateProductIsCheckedAll(isChecked: Boolean)
    suspend fun updateProductIsCheckedById(isChecked: Boolean, id: Int?)
    suspend fun deleteProductByIdFromTrolley(id: Int?)
    suspend fun deleteAllProductFromTrolley(trolley: ProductEntity)
}