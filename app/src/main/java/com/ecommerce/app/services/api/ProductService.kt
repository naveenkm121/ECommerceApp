package com.ecommerce.app.services.api

import com.ecommerce.app.constants.Constants
import com.ecommerce.app.data.product.ProductDetailRes
import com.ecommerce.app.data.product.ProductRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {

  //@GET("${Constants.BASE_URL}${Constants.END_POINT_POKEMONS}")

    @GET("${Constants.BASE_URL}")
    suspend fun  getLaunchStatus():Response<String>

   // @GET("/products")
    //@GET("${Constants.BASE_URL}/products")
   @GET("products")
   suspend fun  getProducts():Response<ProductRes>
   @GET("products?pageSize=2")
    suspend fun  getProductsPage(@Query("pageNo") pageNo:Int):Response<ProductRes>

    @GET("products/{prodId}")
    suspend fun  getProductDetailsById(@Path("prodId") prodId:Int):Response<ProductDetailRes>

}