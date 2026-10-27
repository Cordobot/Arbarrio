package com.arbarrio.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import com.arbarrio.app.ui.components.AnimatedBackground

class AddPropertyScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<AddPropertyViewModel>()
        
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var imageUrl by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Apartamentos") }
        var bedrooms by remember { mutableStateOf("") }
        var bathrooms by remember { mutableStateOf("") }
        var area by remember { mutableStateOf("") }

        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()

        Scaffold(
            containerColor = Color(0xFF0F0F12),
            topBar = {
                TopAppBar(
                    title = { Text("Publicar Propiedad", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (error != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Red.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = error!!, 
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp
                        )
                    }
                }

                // Section Title
                Text("Información Básica", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                PremiumTextField(value = title, onValueChange = { title = it }, label = "Título de la publicación")
                PremiumTextField(value = description, onValueChange = { description = it }, label = "Descripción detallada", minLines = 3)
                PremiumTextField(value = price, onValueChange = { price = it }, label = "Precio (COP)", keyboardType = KeyboardType.Number)

                // Neighborhood Dropdown
                var neighborhoodExpanded by remember { mutableStateOf(false) }
                val medellinNeighborhoods = listOf(
                    "El Poblado", "Laureles", "Belén", "Robledo", "Aranjuez", "Castilla", 
                    "Buenos Aires", "La Candelaria", "Guayabal", "San Cristóbal", 
                    "Santa Cruz", "Popular", "Manrique", "Doce de Octubre", "Villa Hermosa", "San Javier"
                ).sorted()

                Text("Ubicación en Medellín", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                ExposedDropdownMenuBox(
                    expanded = neighborhoodExpanded,
                    onExpandedChange = { neighborhoodExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PremiumTextField(
                        value = location,
                        onValueChange = {},
                        label = "Barrio",
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = neighborhoodExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = neighborhoodExpanded,
                        onDismissRequest = { neighborhoodExpanded = false },
                        modifier = androidx.compose.ui.Modifier.background(Color(0xFF1B1B1F))
                    ) {
                        medellinNeighborhoods.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption, color = Color.White) },
                                onClick = {
                                    location = selectionOption
                                    neighborhoodExpanded = false
                                }
                            )
                        }
                    }
                }

                PremiumTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = "URL de la imagen (Opcional)")

                // Category Dropdown
                var categoryExpanded by remember { mutableStateOf(false) }
                val categoriesList = listOf("Apartamentos", "Casas", "Habitaciones", "Locales", "Apartaestudios")

                Text("Detalles de la Propiedad", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PremiumTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = "Categoría",
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = androidx.compose.ui.Modifier.background(Color(0xFF1B1B1F))
                    ) {
                        categoriesList.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption, color = Color.White) },
                                onClick = {
                                    category = selectionOption
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    PremiumTextField(value = bedrooms, onValueChange = { bedrooms = it }, label = "Hab.", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                    PremiumTextField(value = bathrooms, onValueChange = { bathrooms = it }, label = "Baños", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                    PremiumTextField(value = area, onValueChange = { area = it }, label = "m²", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.addProperty(
                            title = title,
                            description = description,
                            price = price,
                            imageUrl = imageUrl,
                            location = location,
                            category = category,
                            bedrooms = bedrooms,
                            bathrooms = bathrooms,
                            area = area,
                            onSuccess = {
                                navigator.pop()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Publicar Ahora", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PremiumTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        modifier: Modifier = Modifier,
        keyboardType: KeyboardType = KeyboardType.Text,
        minLines: Int = 1,
        readOnly: Boolean = false,
        trailingIcon: @Composable (() -> Unit)? = null
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = Color.Gray) },
            modifier = modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            minLines = minLines,
            readOnly = readOnly,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedContainerColor = Color(0xFF1B1B1F),
                unfocusedContainerColor = Color(0xFF1B1B1F)
            )
        )
    }
}
