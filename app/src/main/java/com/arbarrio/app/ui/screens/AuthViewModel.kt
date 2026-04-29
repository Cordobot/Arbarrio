package com.arbarrio.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arbarrio.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.arbarrio.app.data.local.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val savedEmail = preferencesManager.savedEmail.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ""
    )

    val isRememberMeEnabled = preferencesManager.isRememberMeEnabled.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    fun saveRememberMe(enabled: Boolean, email: String) {
        viewModelScope.launch {
            preferencesManager.saveRememberMeSettings(enabled, email)
        }
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.signInWithEmail(email, password)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun signUpExtended(
        email: String, 
        password: String, 
        name: String, 
        lastName: String, 
        phone: String, 
        idNumber: String, 
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.signUpWithEmail(email, password, name, lastName, phone, idNumber)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit) {
        if (email.isBlank()) {
            _error.value = "Ingresa tu correo para recuperar la clave."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.sendPasswordResetEmail(email)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}
