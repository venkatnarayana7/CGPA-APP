package com.citizenconnect.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.citizenconnect.app.data.repository.AuthRepository
import com.citizenconnect.app.model.User
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AuthRepository(app)
    
    private val _loginResult  = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult
    
    private val _signupResult = MutableLiveData<Result<User>>()
    val signupResult: LiveData<Result<User>> = _signupResult

    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult
    
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    val isLoggedIn get() = repo.isLoggedIn()
    val currentUid get() = repo.currentUid()

    fun login(email: String, password: String) = viewModelScope.launch { 
        _loading.value = true
        _loginResult.value = repo.login(email, password)
        _loading.value = false 
    }

    fun signup(n: String, e: String, p: String, ph: String, c: String) = viewModelScope.launch { 
        _loading.value = true
        _signupResult.value = repo.signup(n, e, p, ph, c)
        _loading.value = false 
    }

    fun updateProfile(user: User) = viewModelScope.launch {
        _loading.value = true
        _updateResult.value = repo.updateUser(user)
        _loading.value = false
        _user.value = user
    }

    suspend fun deleteAccount(): Result<Unit> {
        return repo.deleteAccount()
    }

    fun loadUser() = viewModelScope.launch { 
        _user.value = repo.currentUid()?.let { repo.getUser(it) } 
    }

    fun logout() = repo.logout()
}
