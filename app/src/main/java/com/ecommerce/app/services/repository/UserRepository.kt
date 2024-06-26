package com.ecommerce.app.services.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.ecommerce.app.data.address.AddressReq
import com.ecommerce.app.data.cart.CartItem
import com.ecommerce.app.data.cart.CartReq
import com.ecommerce.app.data.login.LoginReq
import com.ecommerce.app.data.login.SignupReq
import com.ecommerce.app.services.api.ProductService
import com.ecommerce.app.services.remotedata.UserRemoteDataSource
import com.ecommerce.app.ui.paging.ProductPagingSource
import com.ecommerce.app.utils.performGetOperation
import javax.inject.Inject


class UserRepository @Inject constructor(private val userRemoteDataSource: UserRemoteDataSource) {

/*    fun getProductList() = Pager(
        config = PagingConfig(pageSize = 10, maxSize = 100),
        pagingSourceFactory = { ProductPagingSource(productService) }
    ).liveData*/

    fun getLogin(loginReq: LoginReq) = performGetOperation(
        networkCall = { userRemoteDataSource.getLogin(loginReq) }
    )

    fun getSignup(signupReq: SignupReq) = performGetOperation(
        networkCall = { userRemoteDataSource.getSignup(signupReq) }
    )

    fun getWishlist() = performGetOperation(
        networkCall = { userRemoteDataSource.getWishlist() }
    )

    fun getAddressList() = performGetOperation(
        networkCall = { userRemoteDataSource.getAddressList() }
    )

    fun addAddress(addAddressReq: AddressReq) = performGetOperation(
        networkCall = { userRemoteDataSource.addAddress(addAddressReq) }
    )

    fun updateAddress(id: Int,addressReq: AddressReq) = performGetOperation(
        networkCall = { userRemoteDataSource.updateAddress(id,addressReq) }
    )
    fun deleteAddress(id: Int) = performGetOperation(
        networkCall = { userRemoteDataSource.deleteAddress(id) }
    )
    fun getPincodeDetails(pincode:String) = performGetOperation(
        networkCall = { userRemoteDataSource.getPincodeDetails(pincode) }
    )

    fun getCartItems() = performGetOperation(
        networkCall = { userRemoteDataSource.getCartItems() }
    )

    fun deleteCartItem(id: Int) = performGetOperation(
        networkCall = { userRemoteDataSource.deleteCartItem(id) }
    )

    fun addToCart(cartReq: CartReq) = performGetOperation(
        networkCall = { userRemoteDataSource.addToCart(cartReq) }
    )

    fun updateCart(id: Int,cartItem:CartItem) = performGetOperation(
        networkCall = { userRemoteDataSource.updateCart(id,cartItem) }
    )
}