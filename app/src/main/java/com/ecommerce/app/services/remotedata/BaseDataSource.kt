package com.ecommerce.app.services.remotedata

import com.ecommerce.app.utils.DebugHandler
import com.ecommerce.app.utils.ResourceViewState
import retrofit2.Response


abstract class BaseDataSource {

     protected suspend fun <T> getResult(call: suspend () -> Response<T>): ResourceViewState<T> {
         try {
             val response = call()
             if (response.isSuccessful) {
                 val body = response.body()
                 if (body != null) return ResourceViewState.success(body)
             }
             return error(" ${response.code()} ${response.message()}")
         } catch (e: Exception) {
             return error(e.message ?: e.toString())
         }
     }

     private fun <T> error(message: String): ResourceViewState<T> {
         DebugHandler.log(message)
         return ResourceViewState.error("Network call has failed for a following reason: $message")
     }
}