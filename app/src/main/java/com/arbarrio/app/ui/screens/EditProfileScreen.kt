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
import com.arbarrio.app.ui.theme.DarkBg
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.style.TextAlign
import com.arbarrio.app.R
import androidx.compose.ui.res.stringResource

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
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.profile_title), fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
                            Text(if (isEditing) "Hecho" else "Editar", fontWeight = FontWeight.Bold, color = Color(0xFF7C7CFF))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = DarkBg
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // User Avatar with Initials and Camera Icon
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Surface(
                                modifier = Modifier.size(120.dp),
                                shape = CircleShape,
                                color = Color(0xFF6366F1), // Indigo background
                                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.2f))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    val initials = "${name.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase()
                                    Text(
                                        text = if (initials.isNotEmpty()) initials else "AA",
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            // Camera Icon Button
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                color = Color(0xFF3730A3),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Change photo",
                                    modifier = Modifier.padding(6.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Full Name and Badge
                        val fullName = listOfNotNull(user?.name, user?.lastName).joinToString(" ")
                        Text(
                            text = if (fullName.isNotBlank()) fullName else "Adrian Alvarez",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Surface(
                            color = Color(0xFF6366F1).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Arrendatario",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF818CF8)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Section: Personal Information
                        SectionHeader("INFORMACIÓN PERSONAL")
                        
                        ProfileItemCard(
                            label = "Nombre completo",
                            value = "$name $lastName",
                            icon = Icons.Default.Person,
                            enabled = isEditing,
                            onValueChange = { /* Handle combined name editing if needed */ }
                        )
                        
                        ProfileItemCard(
                            label = "Teléfono",
                            value = phone,
                            icon = Icons.Default.Phone,
                            enabled = isEditing,
                            onValueChange = { phone = it }
                        )
                        
                        ProfileItemCard(
                            label = "Edad",
                            value = if (age.isNotEmpty()) "$age años" else "Sin especificar",
                            icon = Icons.Default.CalendarMonth,
                            enabled = isEditing,
                            onValueChange = { age = it.filter { char -> char.isDigit() } }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Section: Documents
                        SectionHeader("DOCUMENTOS")
                        
                        ProfileItemCard(
                            label = "Cédula",
                            value = idNumber,
                            icon = Icons.Default.Badge,
                            enabled = isEditing,
                            onValueChange = { idNumber = it }
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        // Guardar Cambios Button
                        if (isEditing) {
                            Button(
                                onClick = { 
                                    viewModel.updateProfile(name, lastName, phone, idNumber, age) {
                                        isEditing = false
                                        showSuccessMessage = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B1B1F)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Guardar cambios", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }

                    // Success Message
                    if (showSuccessMessage) {
                        LaunchedEffect(Unit) {
                            delay(3000)
                            showSuccessMessage = false
                        }
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(24.dp)
                                .fillMaxWidth(),
                            color = Color(0xFF4CAF50),
                            shape = RoundedCornerShape(12.dp)
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
    fun SectionHeader(title: String) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            letterSpacing = 1.sp
        )
    }

    @Composable
    fun ProfileItemCard(
        label: String,
        value: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        enabled: Boolean,
        onValueChange: (String) -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp),
            color = Color(0xFF1B1B1F).copy(alpha = 0.6f),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF6366F1).copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = Color(0xFF818CF8)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = label, fontSize = 12.sp, color = Color.Gray)
                    if (enabled) {
                        BasicTextField(
                            value = value.removeSuffix(" años"),
                            onValueChange = onValueChange,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.White)
                        )
                    } else {
                        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
