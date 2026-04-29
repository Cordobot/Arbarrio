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

    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties = _properties.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadMyProperties()
    }

    fun loadMyProperties() {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUser = authRepository.currentUser.firstOrNull()
            if (currentUser != null) {
                propertyRepository.getProperties().collect { allProperties ->
                    // Filter properties owned by current user (by phone for now as per previous logic)
                    _properties.value = allProperties.filter { it.ownerPhone == currentUser.phone && it.ownerPhone != null && it.ownerPhone.isNotBlank() }
                }
            }
            _isLoading.value = false
        }
    }
}
