package com.ecommerce.app.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ecommerce.app.data.product.ProductItem
import com.ecommerce.app.services.api.ProductService

class ProductPagingSource(private val productService: ProductService) : PagingSource<Int, ProductItem>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductItem> {
        return try {
            val position = params.key ?: 1
            val response = productService.getProductsPage(position)

            return LoadResult.Page(
                data = response.data,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (position == response.totalPages) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProductItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}