package com.augieafr.storyapp.data.remote

import com.augieafr.storyapp.data.model.ErrorResponse
import com.augieafr.storyapp.data.model.LoginPayload
import com.augieafr.storyapp.data.model.LoginResponse
import com.augieafr.storyapp.data.model.RegisterPayload
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    suspend fun login(@Body loginPayload: LoginPayload): Response<LoginResponse>

    @POST("register")
    suspend fun register(
        @Body registerPayload: RegisterPayload
    ): Response<ErrorResponse>

}