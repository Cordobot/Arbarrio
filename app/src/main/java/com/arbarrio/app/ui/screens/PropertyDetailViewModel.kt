package com.arbarrio.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arbarrio.app.domain.model.Property
import com.arbarrio.app.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PropertyDetailViewModel(
    private val propertyRepository: PropertyRepository,
    private val propertyId: String
) : ViewModel() {

    private val _property = MutableStateFlow<Property?>(null)
    val property = _property.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadProperty()
    }

    private fun loadProperty() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = propertyRepository.getPropertyById(propertyId)
            _property.value = result
            _isLoading.value = false
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _property.value?.let { 
                propertyRepository.toggleFavorite(it.id)
                // Update local state for immediate UI response
                _property.value = it.copy(isFavorite = !it.isFavorite)
            }
        }
    }
}
