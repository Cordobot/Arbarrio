package com.arbarrio.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arbarrio.app.domain.model.Property
import com.arbarrio.app.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.arbarrio.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.arbarrio.app.data.location.LocationService

class HomeViewModel(
    private val propertyRepository: PropertyRepository,
    private val authRepository: AuthRepository,
    private val locationService: LocationService
) : ViewModel() {

    val currentUser = authRepository.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _allProperties = MutableStateFlow<List<Property>>(emptyList())
    
    private val _currentCity = MutableStateFlow("Cargando ubicación...")
    val currentCity = _currentCity.asStateFlow()
    
    val properties = combine(_allProperties, _selectedCategory) { list, category ->
        if (category == "Todos") {
            list
        } else {
            list.filter { it.category.equals(category, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteCount = _allProperties.map { list ->
        list.count { it.isFavorite }.toString()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0")

    val myPropertiesCount = _allProperties.map { list ->
        list.size.toString() // Currently showing all as "mine" for demo, or could filter by ownerId if available
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0")

    private val _categories = MutableStateFlow(listOf("Todos", "Apartamentos", "Casas", "Habitaciones", "Locales", "Apartaestudios"))
    val categories = _categories.asStateFlow()

    init {
        observeProperties()
    }

    private fun observeProperties() {
        viewModelScope.launch {
            propertyRepository.getProperties().collect { 
                _allProperties.value = it
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateLocation() {
        viewModelScope.launch {
            locationService.getCurrentLocationName().collect { name ->
                _currentCity.value = name ?: "Ubicación no disponible"
            }
        }
    }

    fun toggleFavorite(propertyId: String) {
        viewModelScope.launch {
            propertyRepository.toggleFavorite(propertyId)
        }
    }
}
