package com.augieafr.storyapp.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.augieafr.storyapp.data.repository.AuthRepository
import com.augieafr.storyapp.data.utils.ResultState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    var isLoginScreen = true
    val firstTimeCheckToken = Job()
    val formValidMap = hashMapOf<Int, Boolean>()

    val getToken = repository.currentUserToken

    private val _authUIState = MutableLiveData<ResultState<Boolean>>()
    val authUIState: LiveData<ResultState<Boolean>>
        get() = _authUIState

    fun login(email: String, password: String) = viewModelScope.launch {
        repository.login(email, password).collectLatest {
            _authUIState.value = it
        }
    }

    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        repository.register(name, email, password).collectLatest {
            _authUIState.value = it
        }
    }

    fun updateFormErrorState(errorState: Pair<Int, Boolean>) {
        formValidMap[errorState.first] = errorState.second
    }
}