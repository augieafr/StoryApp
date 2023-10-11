package com.augieafr.storyapp.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.augieafr.storyapp.data.local.preferences.UserPreference
import com.augieafr.storyapp.data.model.ErrorResponse
import com.augieafr.storyapp.data.model.LoginPayload
import com.augieafr.storyapp.data.model.RegisterPayload
import com.augieafr.storyapp.data.remote.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) : ViewModel() {

    var isLoginScreen = true

    val getToken = userPreference.getUserToken()

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String>
        get() = _isError

    private val _isSuccessRegister: MutableLiveData<Boolean> = MutableLiveData()
    val isSuccessRegister: LiveData<Boolean>
        get() = _isSuccessRegister

    fun login(email: String, password: String) = viewModelScope.launch {
        _isLoading.value = true

        withContext(Dispatchers.IO) {
            val result = apiService.login(LoginPayload(email, password))
            if (result.isSuccessful) {
                _isLoading.postValue(false)
                result.body()?.let {
                    if (!it.error) {
                        userPreference.setUserToken("Bearer " + it.loginResult.token)
                    } else {
                        _isError.postValue(it.message)
                    }
                }
            } else {
                val errorResponse =
                    Gson().fromJson(result.errorBody()?.string(), ErrorResponse::class.java)
                _isError.postValue(errorResponse.message)
                _isLoading.postValue(false)
            }
        }
    }

    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        _isLoading.value = true

        withContext(Dispatchers.IO) {
            val result = apiService.register(RegisterPayload(name, email, password))
            if (result.isSuccessful) {
                _isLoading.postValue(false)
                result.body()?.let {
                    if (!it.error) {
                        _isSuccessRegister.postValue(true)
                    } else {
                        _isError.postValue(it.message)
                    }
                }
            } else {
                val errorResponse =
                    Gson().fromJson(result.errorBody()?.string(), ErrorResponse::class.java)
                _isError.postValue(errorResponse.message)
                _isLoading.postValue(false)
            }
        }
    }
}