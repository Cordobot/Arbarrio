package com.arbarrio.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.arbarrio.app.domain.model.Property
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

import org.koin.androidx.compose.koinViewModel

class MyPropertiesScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<MyPropertiesViewModel>()
        val properties by viewModel.properties.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        var propertyToDelete by remember { mutableStateOf<Property?>(null) }

        if (propertyToDelete != null) {
            AlertDialog(
                onDismissRequest = { propertyToDelete = null },
                title = { Text("Eliminar publicación") },
                text = { Text("¿Estás seguro de que deseas eliminar esta propiedad? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = { 
                        propertyToDelete?.let { viewModel.deleteProperty(it.id) }
                        propertyToDelete = null 
                    }) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { propertyToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        Scaffold(
            containerColor = Color(0xFF0F0F12),
            topBar = {
                TopAppBar(
                    title = { Text("Mis Propiedades", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF6366F1))
                } else if (properties.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            color = Color(0xFF1B1B1F),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.padding(30.dp),
                                tint = Color(0xFF6366F1).copy(alpha = 0.2f)
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "No tienes propiedades",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Publica tu primera propiedad y comienza a recibir interesados.",
                            fontSize = 15.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(48.dp))
                        Button(
                            onClick = { navigator.push(AddPropertyScreen()) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                        ) {
                            Text("Publicar Ahora", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(properties) { property ->
                            HomeScreen().PropertyCard(
                                property = property,
                                onFavoriteClick = { viewModel.toggleFavorite(property.id) },
                                onClick = { navigator.push(PropertyDetailScreen(property.id)) },
                                onDeleteClick = { propertyToDelete = property }
                            )
                        }
                    }
                }
            }
        }
    }
}
