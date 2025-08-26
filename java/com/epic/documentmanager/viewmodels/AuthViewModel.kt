package com.epic.documentmanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic.documentmanager.models.User
import com.epic.documentmanager.repositories.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    private val _registerResult = MutableLiveData<Result<User>>()
    val registerResult: LiveData<Result<User>> = _registerResult

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _passwordUpdateResult = MutableLiveData<Result<Unit>>()
    val passwordUpdateResult: LiveData<Result<Unit>> = _passwordUpdateResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = authRepository.loginUser(email, password)
                if (result.isSuccess) {
                    val user = authRepository.getCurrentUser()
                    if (user != null) {
                        _currentUser.value = user
                        _loginResult.value = Result.success(user)
                    } else {
                        _loginResult.value = Result.failure(Exception("Failed to get user data"))
                    }
                } else {
                    _loginResult.value = Result.failure(result.exceptionOrNull() ?: Exception("Login failed"))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun register(email: String, password: String, fullName: String, role: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = authRepository.registerUser(email, password, fullName, role)
                if (result.isSuccess) {
                    val user = authRepository.getCurrentUser()
                    if (user != null) {
                        _currentUser.value = user
                        _registerResult.value = Result.success(user)
                    } else {
                        _registerResult.value = Result.failure(Exception("Failed to get user data"))
                    }
                } else {
                    _registerResult.value = Result.failure(result.exceptionOrNull() ?: Exception("Registration failed"))
                }
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                _currentUser.value = user
            } catch (e: Exception) {
                _currentUser.value = null
            }
        }
    }

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = authRepository.updatePassword(newPassword)
                _passwordUpdateResult.value = result
            } catch (e: Exception) {
                _passwordUpdateResult.value = Result.failure(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _currentUser.value = null
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}