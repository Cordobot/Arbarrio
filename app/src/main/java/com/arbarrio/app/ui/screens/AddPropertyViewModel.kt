package com.arbarrio.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arbarrio.app.domain.model.Property
import com.arbarrio.app.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddPropertyViewModel(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun addProperty(
        title: String,
        description: String,
        price: String,
        imageUrl: String,
        location: String,
        category: String,
        bedrooms: String,
        bathrooms: String,
        area: String,
        onSuccess: () -> Unit
    ) {
        if (title.isBlank() || price.isBlank() || location.isBlank() || category.isBlank()) {
            _error.value = "Por favor, completa los campos requeridos."
            return
        }

        val priceDouble = price.toDoubleOrNull()
        val bedroomsInt = bedrooms.toIntOrNull() ?: 0
        val bathroomsInt = bathrooms.toIntOrNull() ?: 0
        val areaInt = area.toIntOrNull() ?: 0

        if (priceDouble == null) {
            _error.value = "El precio no es válido."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val property = Property(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                price = priceDouble,
                imageUrl = if (imageUrl.isBlank()) "https://via.placeholder.com/400" else imageUrl,
                location = location,
                category = category,
                bedrooms = bedroomsInt,
                bathrooms = bathroomsInt,
                area = areaInt
            )

            val result = propertyRepository.addProperty(property)
            _isLoading.value = false

            result.fold(
                onSuccess = { onSuccess() },
                onFailure = { _error.value = it.message ?: "Error al publicar propiedad." }
            )
        }
    }
}
