package com.augieafr.storyapp.data.remote

import com.augieafr.storyapp.data.model.DetailResponse
import com.augieafr.storyapp.data.model.ErrorResponse
import com.augieafr.storyapp.data.model.ListStoryResponse
import com.augieafr.storyapp.data.model.LoginPayload
import com.augieafr.storyapp.data.model.LoginResponse
import com.augieafr.storyapp.data.model.RegisterPayload
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("login")
    suspend fun login(@Body loginPayload: LoginPayload): Response<LoginResponse>

    @POST("register")
    suspend fun register(
        @Body registerPayload: RegisterPayload
    ): Response<ErrorResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String
    ): Response<ListStoryResponse>

    @POST("stories")
    @Multipart
    suspend fun createStory(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Response<ErrorResponse>

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<DetailResponse>
}