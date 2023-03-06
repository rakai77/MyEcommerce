package com.example.myecommerce.data.api.service

import com.example.myecommerce.data.model.DataStock
import com.example.myecommerce.data.model.UpdateRate
import com.example.myecommerce.data.response.*
import retrofit2.http.*

interface ApiProduct {

    @GET("get_list_product")
    suspend fun getListProduct(
        @Query("search") query: String?
    ): ListProductResponse

    @GET("get_list_product_paging")
    suspend fun getListProductPaging(
        @Query("search") query: String?,
        @Query("offset") offset: Int
    ): ListProductResponse

    @GET("get_list_product_favorite")
    suspend fun getListProductFavorite(
        @Query("search") query: String?,
        @Query("id_user") userId: Int
    ): ListFavoriteProductResponse

    @GET("get_detail_product")
    suspend fun getDetailProduct(
        @Query("id_product") productId: Int,
        @Query("id_user") userId: Int,
    ): DetailProductResponse

    @FormUrlEncoded
    @POST("add_favorite")
    suspend fun addFavoriteProduct(
        @Field("id_product") productId: Int,
        @Field("id_user") userId: Int
    ): AddFavoriteResponse

    @FormUrlEncoded
    @POST("remove_favorite")
    suspend fun removeFavoriteProduct(
        @Field("id_product") productId: Int,
        @Field("id_user") userId: Int
    ): RemoveFavoriteResponse

    @POST("update-stock")
    suspend fun updateStock(
        @Body updateStock: DataStock
    ): UpdateStockResponse

    @PUT("update_rate/{id}")
    suspend fun updateRate(
        @Path("id") id: Int,
        @Body updateRate: UpdateRate
    ): UpdateRateResponse
}