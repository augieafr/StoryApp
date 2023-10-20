package com.augieafr.storyapp.data.utils

import com.augieafr.storyapp.data.local.preferences.UserPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take

abstract class RepositoryWithToken(val userPreference: UserPreference) {
    protected inline fun <T> executeRequest(
        crossinline action: suspend (FlowCollector<ResultState<T>>, String) -> Unit
    ) =
        flow {
            emit(ResultState.Loading(true))
            userPreference.getUserToken().take(1).collect { token ->
                action(this, token)
            }
        }.catch {
            emit(ResultState.Loading(false))
            emit(ResultState.Error(it.message.orEmpty()))
        }.flowOn(Dispatchers.IO)
}