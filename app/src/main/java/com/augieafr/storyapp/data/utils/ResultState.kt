package com.augieafr.storyapp.data.utils

sealed class ResultState<T> {
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error<T>(val errorMessage: String) : ResultState<T>()
    class Loading<T>(val isLoading: Boolean) : ResultState<T>()
}