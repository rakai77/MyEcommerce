package com.example.myecommerce.data.repository.product

import androidx.paging.PagingSource
import com.example.myecommerce.data.model.DataStock
import com.example.myecommerce.data.Result
import com.example.myecommerce.data.api.service.ApiProduct
import com.example.myecommerce.data.local.room.ProductDao
import com.example.myecommerce.data.local.room.ProductEntity
import com.example.myecommerce.data.model.UpdateRate
import com.example.myecommerce.data.response.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class ProductRepositoryImp @Inject constructor(
    private val apiProduct: ApiProduct,
    private val productDao: ProductDao
) :
    ProductRepository {

    override fun getListProduct(product: String?): Flow<Result<ListProductResponse>> = flow {
        emit(Result.Loading)
        try {
            val result = apiProduct.getListProduct(product)
            emit(Result.Success(result))
        } catch (t: Throwable) {
            if (t is HttpException) {
                when (t.code()) {
                    400 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    404 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    500 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    else -> emit(Result.Error(null, null, null))
                }
            }
        }
    }

    override fun getListProductPaging(query: String): PagingSource<Int, DataItem> {
        TODO("Not yet implemented")
    }

    override fun getListFavoriteProduct(
        product: String?,
        userID: Int
    ): Flow<Result<ListFavoriteProductResponse>> = flow {
        emit(Result.Loading)
        try {
            val result = apiProduct.getListProductFavorite(product, userID)
            emit(Result.Success(result))
        } catch (t: Throwable) {
            if (t is HttpException) {
                when (t.code()) {
                    400 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    404 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    500 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    else -> emit(Result.Error(null, null, null))
                }
            }
        }
    }

    override fun getDetailProduct(
        productID: Int,
        userID: Int
    ): Flow<Result<DetailProductResponse>> = flow {
        emit(Result.Loading)
        try {
            val result = apiProduct.getDetailProduct(productID, userID)
            emit(Result.Success(result))
        } catch (t: Throwable) {
            if (t is HttpException) {
                when (t.code()) {
                    400 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    404 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    500 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    else -> emit(Result.Error(null, null, null))
                }
            }
        }
    }

    override fun addFavoriteProduct(
        productID: Int,
        userID: Int
    ): Flow<Result<AddFavoriteResponse>> = flow {
        emit(Result.Loading)
        try {
            val result = apiProduct.addFavoriteProduct(productID, userID)
            emit(Result.Success(result))
        } catch (t: Throwable) {
            if (t is HttpException) {
                when (t.code()) {
                    400 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    404 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    500 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    else -> emit(Result.Error(null, null, null))
                }
            }
        }
    }

    override fun removeFavoriteProduct(
        productID: Int,
        userID: Int
    ): Flow<Result<RemoveFavoriteResponse>> = flow {
        emit(Result.Loading)
        try {
            val result = apiProduct.removeFavoriteProduct(productID, userID)
            emit(Result.Success(result))
        } catch (t: Throwable) {
            if (t is HttpException) {
                when (t.code()) {
                    400 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    404 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    500 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    else -> emit(Result.Error(null, null, null))
                }
            }
        }
    }

    override fun updateStock(updateStock: DataStock): Flow<Result<UpdateStockResponse>> = flow {
        emit(Result.Loading)
        try {
            val result = apiProduct.updateStock(updateStock)
            emit(Result.Success(result))
        } catch (t: Throwable) {
            if (t is HttpException) {
                when (t.code()) {
                    400 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    404 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    500 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    else -> emit(Result.Error(null, null, null))
                }
            }
        }
    }

    override fun updateRate(
        id: Int,
        updateRate: UpdateRate
    ): Flow<Result<UpdateRateResponse>> = flow {
        emit(Result.Loading)
        try {
            val result = apiProduct.updateRate(id, updateRate)
            emit(Result.Success(result))
        } catch (t: Throwable) {
            if (t is HttpException) {
                when (t.code()) {
                    400 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    404 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    500 -> emit(Result.Error(t.message(), t.code(), t.response()?.errorBody()))
                    else -> emit(Result.Error(null, null, null))
                }
            }
        }
    }

    override fun getAllProduct(): Flow<List<ProductEntity>> {
        return productDao.getAllProduct()
    }

    override fun getAllCheckedProduct(): Flow<List<ProductEntity>> {
        return productDao.getALlProductIsChecked()
    }

    override fun getProductById(id: Int?): Flow<List<ProductEntity>> {
        return productDao.getProductById(id)
    }

    override suspend fun addProductToTrolley(trolley: ProductEntity) {
        productDao.addProductToTrolley(trolley)
    }

    override suspend fun updateProductData(quantity: Int?, itemTotalPrice: Int?, id: Int?) {
        productDao.updateProductData(quantity, itemTotalPrice, id)
    }

    override suspend fun updateProductIsCheckedAll(isChecked: Boolean) {
        productDao.updateProductIsCheckedAll(isChecked)
    }

    override suspend fun updateProductIsCheckedById(isChecked: Boolean, id: Int?) {
        productDao.updateProductIsCheckedById(isChecked, id)
    }

    override suspend fun deleteProductByIdFromTrolley(id: Int?) {
        productDao.deleteProductById(id)
    }

    override suspend fun deleteAllProductFromTrolley(trolley: ProductEntity) {
        productDao.deleteProductFromTrolley(trolley)
    }
}