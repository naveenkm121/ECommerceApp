package com.ecommerce.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.ecommerce.app.constants.RequestApiType
import com.ecommerce.app.constants.ScreenName
import com.ecommerce.app.data.address.AddressDataRes
import com.ecommerce.app.data.address.AddressReq
import com.ecommerce.app.data.address.AddressRes
import com.ecommerce.app.services.repository.UserRepository
import com.ecommerce.app.utils.DebugHandler
import com.ecommerce.app.utils.ResourceViewState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private var addressId: Int = 0
    private var requestType: String = ""

    private val _request = MutableLiveData<String>()
    private val _request_address = MutableLiveData<AddressReq>()

    private val _response = _request.switchMap { request ->
        userRepository.getAddressList()

    }

    private val _response_address = _request_address.switchMap { request ->

        when (requestType) {

            RequestApiType.REQUEST_ADD_ADDRESS.value -> {
                userRepository.addAddress(request)
            }

            RequestApiType.REQUEST_UPDATE_ADDRESS.value -> {
                userRepository.updateAddress(addressId, request)
            }

            RequestApiType.REQUEST_DELETE_ADDRESS.value -> {
                userRepository.deleteAddress(addressId)
            }

            else -> throw AssertionError()
        }.map { response ->
            ResourceViewState(response.status, response.data, response.message, requestType)
        }

    }


    val response: LiveData<ResourceViewState<AddressRes>> = _response
    val responseAddress: LiveData<ResourceViewState<AddressDataRes>> = _response_address

    fun getAddress(request: String?) {
        DebugHandler.log("CommonReq ::  $request")
        _request.value = request!!

    }

    fun deleteAddress(requestType: String, addressId: Int) {
        DebugHandler.log("CommonReq ::  $addressId")
        this.requestType = requestType
        this.addressId = addressId
        // _request_address.value = request!!
    }

    fun updateAddress(requestType: String, request: AddressReq, addId: Int) {
        val req: String = Gson().toJson(request)
        DebugHandler.log("CommonReq ::  $req ")
        DebugHandler.log("requestType ::  $requestType AddressId=$addId")
        this.requestType = requestType
        this.addressId = addId
        this._request_address.value = request


    }

}