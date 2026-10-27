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
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            _error.value = "Por favor, ingresa tu correo y contraseña."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.signInWithEmail(trimmedEmail, trimmedPassword)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = it.message ?: "Error al iniciar sesión. Verifica tus credenciales." }
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
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()
        val trimmedName = name.trim()
        val trimmedLastName = lastName.trim()
        val trimmedPhone = phone.trim()
        val trimmedId = idNumber.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank() || trimmedName.isBlank() || trimmedLastName.isBlank()) {
            _error.value = "Por favor, completa todos los campos obligatorios."
            return
        }

        if (trimmedPassword.length < 6) {
            _error.value = "La contraseña debe tener al menos 6 caracteres."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.signUpWithEmail(trimmedEmail, trimmedPassword, trimmedName, trimmedLastName, trimmedPhone, trimmedId)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = it.message ?: "Error al crear la cuenta. Intenta de nuevo." }
            _isLoading.value = false
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            _error.value = "Ingresa tu correo para recuperar la clave."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.sendPasswordResetEmail(trimmedEmail)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = it.message ?: "No se pudo enviar el correo de recuperación." }
            _isLoading.value = false
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.signInWithGoogle(idToken)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = it.message ?: "Error al iniciar sesión con Google." }
            _isLoading.value = false
        }
    }

    fun signInWithFacebook(accessToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.signInWithFacebook(accessToken)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = it.message ?: "Error al iniciar sesión con Facebook." }
            _isLoading.value = false
        }
    }
}
