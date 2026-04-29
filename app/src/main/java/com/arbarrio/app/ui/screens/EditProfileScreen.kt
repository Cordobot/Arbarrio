package com.arbarrio.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arbarrio.app.ui.components.AnimatedBackground
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.delay

class EditProfileScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<EditProfileViewModel>()
        val homeViewModel = koinViewModel<HomeViewModel>()
        val user by homeViewModel.currentUser.collectAsState()

        var isEditing by remember { mutableStateOf(false) }
        var name by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var idNumber by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var showSuccessMessage by remember { mutableStateOf(false) }

        // Load initial data
        LaunchedEffect(user) {
            user?.let {
                name = it.name ?: ""
                lastName = it.lastName ?: ""
                phone = it.phone ?: ""
                idNumber = it.idNumber ?: ""
                age = it.age?.toString() ?: ""
            }
        }

        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Mi Perfil") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        TextButton(onClick = { 
                            if (isEditing) {
                                viewModel.updateProfile(name, lastName, phone, idNumber, age) {
                                    isEditing = false
                                    showSuccessMessage = true
                                }
                            } else {
                                isEditing = true
                            }
                        }) {
                            Text(if (isEditing) "Guardar" else "Editar", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        ) { padding ->
            AnimatedBackground {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // User Avatar with Border
                        Surface(
                            modifier = Modifier
                                .size(120.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            border = androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.padding(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        if (error != null) {
                            Text(text = error!!, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        ProfileField(label = "Nombre", value = name, onValueChange = { name = it }, enabled = isEditing)
                        ProfileField(label = "Apellido", value = lastName, onValueChange = { lastName = it }, enabled = isEditing)
                        ProfileField(label = "Teléfono", value = phone, onValueChange = { phone = it }, enabled = isEditing)
                        ProfileField(label = "Cédula", value = idNumber, onValueChange = { idNumber = it }, enabled = isEditing)
                        ProfileField(label = "Edad", value = age, onValueChange = { age = it }, enabled = isEditing)

                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }

                    // Success Message SnackBar-like
                    if (showSuccessMessage) {
                        LaunchedEffect(Unit) {
                            delay(3000)
                            showSuccessMessage = false
                        }
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                                .fillMaxWidth(),
                            color = Color(0xFF4CAF50), // Green
                            shape = RoundedCornerShape(12.dp),
                            tonalElevation = 4.dp
                        ) {
                            Text(
                                text = "¡Perfil actualizado con éxito!",
                                color = Color.White,
                                modifier = Modifier.padding(16.dp),
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit, enabled: Boolean) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    disabledContainerColor = Color.Transparent
                ),
                placeholder = { Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)) }
            )
        }
    }
}
