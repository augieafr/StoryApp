package com.augieafr.storyapp.data.repository

import com.augieafr.storyapp.data.local.preferences.UserPreference
import com.augieafr.storyapp.data.model.payload.LoginPayload
import com.augieafr.storyapp.data.model.payload.RegisterPayload
import com.augieafr.storyapp.data.remote.ApiService
import com.augieafr.storyapp.data.utils.EspressoIdlingResource
import com.augieafr.storyapp.data.utils.ResultState
import com.augieafr.storyapp.data.utils.getException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AuthRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    val currentUserToken = userPreference.getUserToken()

    fun login(email: String, password: String) = execute<Boolean> { flowCollector ->
        val result = apiService.login(LoginPayload(email, password))
        flowCollector.emit(ResultState.Loading(false))
        if (result.isSuccessful) {
            result.body()?.let {
                userPreference.setUserToken("Bearer " + it.loginResult.token)
            }
        } else {
            flowCollector.emit(ResultState.Error(result.getException()))
        }
    }

    fun register(name: String, email: String, password: String) =
        execute { flowCollector ->
            val result = apiService.register(RegisterPayload(name, email, password))
            flowCollector.emit(ResultState.Loading(false))
            if (result.isSuccessful) {
                result.body()?.let {
                    flowCollector.emit(ResultState.Success(true))
                }
            } else {
                flowCollector.emit(ResultState.Error(result.getException()))
            }
        }

    private inline fun <T> execute(
        crossinline action: suspend (FlowCollector<ResultState<T>>) -> Unit
    ) =
        flow {
            EspressoIdlingResource.increment()
            emit(ResultState.Loading(true))
            action(this)
        }.catch {
            emit(ResultState.Loading(false))
            emit(ResultState.Error(it))
        }.flowOn(Dispatchers.IO)
}