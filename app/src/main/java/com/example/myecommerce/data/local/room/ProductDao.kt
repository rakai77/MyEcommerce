package com.example.myecommerce.data.local.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProductToTrolley(trolley: ProductEntity)

    @Query("SELECT * FROM product")
    fun getAllProduct() : Flow<List<ProductEntity>>

    @Query("SELECT * FROM product WHERE is_checked = '1'")
    fun getALlProductIsChecked(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product WHERE id = :id")
    fun getProductById(id: Int?) : Flow<List<ProductEntity>>

    @Query("UPDATE product SET quantity = :quantity, total_prize = :totalPrize WHERE id = :id")
    suspend fun updateProductData(quantity: Int?, totalPrize: Int?, id: Int?)

    @Query("UPDATE product SET is_checked = :isChecked")
    suspend fun updateProductIsCheckedAll(isChecked: Boolean)

    @Query("UPDATE product SET is_checked = :isChecked WHERE id = :id")
    suspend fun updateProductIsCheckedById(isChecked: Boolean, id: Int?)

    @Query("DELETE FROM product WHERE id = :id")
    suspend fun deleteProductById(id: Int?)

    @Delete
    suspend fun deleteProductFromTrolley(trolley: ProductEntity)

}