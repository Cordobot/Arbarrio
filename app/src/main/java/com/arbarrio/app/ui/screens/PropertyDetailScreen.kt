package com.arbarrio.app.ui.screens

import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.text.NumberFormat
import java.util.*

import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

data class PropertyDetailScreen(val propertyId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<PropertyDetailViewModel> { parametersOf(propertyId) }
        val property by viewModel.property.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val context = LocalContext.current
        val scrollState = rememberScrollState()

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F12))) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF6366F1))
            } else if (property == null) {
                Text("Propiedad no encontrada", modifier = Modifier.align(Alignment.Center), color = Color.White)
            } else {
                val p = property!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Header Image
                    Box(modifier = Modifier.height(350.dp)) {
                        AsyncImage(
                            model = p.imageUrl,
                            contentDescription = p.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Property Info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                            text = p.category.uppercase(),
                                color = Color(0xFF6366F1),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                            val formattedPrice = try {
                                NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO")).format(p.price)
                            } catch (_: Exception) {
                                "$${p.price}"
                            }
                            Text(
                                text = formattedPrice,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF6366F1)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = p.title,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 34.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = p.location,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Key Features Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FeatureItem(label = "Habitaciones", value = p.bedrooms.toString())
                            FeatureItem(label = "Baños", value = p.bathrooms.toString())
                            FeatureItem(label = "Área", value = "${p.area} m²")
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        Text(
                            text = "Descripción",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = p.description.ifBlank { "Sin descripción disponible." },
                            fontSize = 16.sp,
                            color = Color.Gray,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        // Location Section
                        Text(
                            text = "Ubicación",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val lat = if (p.latitude != 0.0 && !p.latitude.isNaN()) p.latitude else 6.2442
                        val lng = if (p.longitude != 0.0 && !p.longitude.isNaN()) p.longitude else -75.5812
                        val propertyLatLng = LatLng(lat, lng)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(propertyLatLng, 15f)
                        }
                        val markerState = rememberMarkerState(position = propertyLatLng)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFF1B1B1F))
                        ) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                uiSettings = MapUiSettings(zoomControlsEnabled = false)
                            ) {
                                Marker(
                                    state = markerState,
                                    title = p.title,
                                    snippet = p.location
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(140.dp))
                    }
                }

                // Top Toolbar (Overlay)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { navigator.pop() },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    IconButton(
                        onClick = { viewModel.toggleFavorite() },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (p.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, 
                            contentDescription = "Favorite", 
                            tint = if (p.isFavorite) Color.Red else Color.White
                        )
                    }
                }

                // Bottom Action Bar
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = Color(0xFF1B1B1F).copy(alpha = 0.95f),
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(24.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { 
                                p.ownerPhone?.let { phone ->
                                    if (phone.isNotBlank()) {
                                        // Clean phone: remove spaces, dashes, +
                                        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
                                        // Add Colombia country code if missing
                                        val intlPhone = if (cleanPhone.startsWith("57")) cleanPhone else "57$cleanPhone"
                                        val waIntent = Intent(
                                            Intent.ACTION_VIEW,
                                            "https://wa.me/$intlPhone".toUri()
                                        )
                                        context.startActivity(waIntent)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366) // WhatsApp green
                            )
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text("Contactar por WhatsApp", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun FeatureItem(label: String, value: String) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .border(1.dp, Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                .padding(16.dp)
                .width(90.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
