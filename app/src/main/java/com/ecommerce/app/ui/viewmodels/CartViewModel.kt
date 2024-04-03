package com.ecommerce.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.ecommerce.app.data.cart.CartRes
import com.ecommerce.app.services.repository.UserRepository
import com.ecommerce.app.utils.DebugHandler
import com.ecommerce.app.utils.ResourceViewState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class CartViewModel @Inject constructor(private val userRepository: UserRepository):ViewModel() {


    private val _request = MutableLiveData<String>()

    private val _response = _request.switchMap {
        userRepository.getCartItems()

    }
    val response: LiveData<ResourceViewState<CartRes>> = _response

    fun getCartItems(request: String) {
        val req: String = Gson().toJson(request)
        DebugHandler.log("CommonReq ::  $req")
        _request.value = request
    }


}