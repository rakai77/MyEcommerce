package com.example.myecommerce.data.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myecommerce.data.api.service.ApiProduct
import com.example.myecommerce.data.response.DataItem
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PagingDataSource @Inject constructor(
    private val query: String?,
    private val apiProduct: ApiProduct
) : PagingSource<Int, DataItem>() {

    override fun getRefreshKey(state: PagingState<Int, DataItem>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataItem> {
        return try {
            val productOffset = params.key ?: OFFSET
            val response = apiProduct.getListProductPaging(query, productOffset)

            val productResult = response.success.data

            val prevKey = if (productOffset == OFFSET) null else productOffset - 1
            val nextKey = if (productResult.isEmpty()) null else productOffset + LIMIT

            LoadResult.Page(
                data = productResult,
                prevKey = prevKey,
                nextKey = nextKey
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    companion object {
        const val OFFSET = 0
        const val LIMIT = 5
    }
}