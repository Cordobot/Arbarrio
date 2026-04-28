package com.arbarrio.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arbarrio.app.domain.model.Property
import com.arbarrio.app.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties = _properties.asStateFlow()

    private val _categories = MutableStateFlow(listOf("Todos", "Apartamentos", "Casas", "Habitaciones", "Locales"))
    val categories = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory = _selectedCategory.asStateFlow()

    init {
        observeProperties()
    }

    private fun observeProperties() {
        viewModelScope.launch {
            propertyRepository.getProperties().collect { 
                _properties.value = it
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }
}
