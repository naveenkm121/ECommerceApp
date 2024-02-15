package com.ecommerce.app.services.api

import com.ecommerce.app.data.login.LoginReq
import com.ecommerce.app.data.login.LoginRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("auth/login")
    suspend fun getLogin(@Body request: LoginReq): Response<LoginRes>
}