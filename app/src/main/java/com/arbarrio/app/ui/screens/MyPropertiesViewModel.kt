package com.arbarrio.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arbarrio.app.domain.model.Property
import com.arbarrio.app.domain.repository.PropertyRepository
import com.arbarrio.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MyPropertiesViewModel(
    private val propertyRepository: PropertyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val properties = combine(
        propertyRepository.getProperties(),
        authRepository.currentUser
    ) { allProperties, user ->
        val userPhone = user?.phone?.trim() ?: ""
        if (userPhone.isEmpty()) {
            emptyList()
        } else {
            allProperties.filter { 
                val ownerPhone = it.ownerPhone?.trim() ?: ""
                // Show properties owned by user OR orphaned ones so they can be cleaned up
                ownerPhone == userPhone || ownerPhone.isEmpty() 
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleFavorite(propertyId: String) {
        viewModelScope.launch {
            propertyRepository.toggleFavorite(propertyId)
        }
    }

    fun deleteProperty(propertyId: String) {
        viewModelScope.launch {
            propertyRepository.deleteProperty(propertyId)
        }
    }
}
