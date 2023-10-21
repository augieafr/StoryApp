package com.augieafr.storyapp.data.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("loginResult")
    val loginResult: LoginResult,

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,
)

data class LoginResult(
    @field:SerializedName("userID")
    val userId: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("token")
    val token: String
)