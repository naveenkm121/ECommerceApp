package com.ecommerce.app.services.remotedata

import com.ecommerce.app.services.api.ProductService
import javax.inject.Inject

class ProductRemoteDataSource @Inject constructor( private  val productService: ProductService) :
    BaseDataSource() {
    suspend fun getPokemons() = getResult { productService.getPokemons()}

    suspend fun getLaunchStatus()=getResult { productService.getLaunchStatus() }
    suspend fun getProducts()=getResult { productService.getProducts() }

}