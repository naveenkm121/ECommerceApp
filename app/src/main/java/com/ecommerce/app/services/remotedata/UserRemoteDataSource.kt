package com.ecommerce.app.services.remotedata

import com.ecommerce.app.data.address.AddressReq
import com.ecommerce.app.data.cart.CartItem
import com.ecommerce.app.data.cart.CartReq
import com.ecommerce.app.data.login.LoginReq
import com.ecommerce.app.data.login.SignupReq
import com.ecommerce.app.services.api.UserService
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(private val userService: UserService): BaseDataSource() {

    suspend fun getLogin( loginReq: LoginReq)=getResult { userService.getLogin(loginReq) }

    suspend fun getSignup( signupReq: SignupReq)=getResult { userService.getSignup(signupReq) }

    suspend fun getWishlist( )=getResult { userService.getWishlist() }

    suspend fun getAddressList( )=getResult { userService.getAddressList() }

    suspend fun addAddress(addressReq: AddressReq)=getResult { userService.addAddress(addressReq) }

    suspend fun updateAddress(id: Int,addressReq: AddressReq)=getResult { userService.updateAddress(id,addressReq) }

    suspend fun deleteAddress(id: Int)=getResult { userService.deleteAddress(id) }

    suspend fun getPincodeDetails(pincode:String )=getResult { userService.getPincodeDetails(pincode) }

    suspend fun getCartItems( )=getResult { userService.getCartItems() }

    suspend fun deleteCartItem(id: Int)=getResult { userService.deleteCartItem(id) }

    suspend fun addToCart(cartReq: CartReq)=getResult { userService.addToCart(cartReq) }

    suspend fun updateCart(id: Int,cartItem: CartItem)=getResult { userService.updateCart(id,cartItem) }
}