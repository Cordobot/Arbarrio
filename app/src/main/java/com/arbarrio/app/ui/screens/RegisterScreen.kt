package com.arbarrio.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arbarrio.app.ui.components.AnimatedBackground
import org.koin.androidx.compose.koinViewModel

class RegisterScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<AuthViewModel>()

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var idNumber by remember { mutableStateOf("") }

        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Registro", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            AnimatedBackground {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Crea tu cuenta",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = "Únete a la comunidad de Arbarrio",
                        fontSize = 16.sp,
                        color = Color(0xFF7C7CFF),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (error != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF442726),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = error!!, color = Color(0xFFF87171), modifier = Modifier.padding(12.dp), fontSize = 14.sp)
                        }
                    }

                    RegisterField(label = "Nombre", value = name, onValueChange = { name = it }, icon = Icons.Default.Person, placeholder = "Ej: Juan")
                    RegisterField(label = "Apellido", value = lastName, onValueChange = { lastName = it }, icon = Icons.Default.Person, placeholder = "Ej: Pérez")
                    RegisterField(label = "Cédula", value = idNumber, onValueChange = { idNumber = it }, icon = Icons.Default.Badge, placeholder = "Número de documento", keyboardType = KeyboardType.Number)
                    RegisterField(label = "Teléfono", value = phone, onValueChange = { phone = it }, icon = Icons.Default.Phone, placeholder = "Ej: 300 123 4567", keyboardType = KeyboardType.Phone)
                    RegisterField(label = "Email", value = email, onValueChange = { email = it }, icon = Icons.Default.Email, placeholder = "ejemplo@correo.com", keyboardType = KeyboardType.Email)
                    RegisterField(label = "Contraseña", value = password, onValueChange = { password = it }, icon = Icons.Default.Lock, placeholder = "Mínimo 6 caracteres", isPassword = true)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.signUpExtended(email, password, name, lastName, phone, idNumber) {
                                navigator.replaceAll(HomeScreen())
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C7CFF)),
                        enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Registrarse", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    @Composable
    fun RegisterField(
        label: String, 
        value: String, 
        onValueChange: (String) -> Unit, 
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        placeholder: String,
        isPassword: Boolean = false,
        keyboardType: KeyboardType = KeyboardType.Text
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(label, color = Color(0xFF7C7CFF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.5f)) },
                visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFF1B1B1F),
                    focusedContainerColor = Color(0xFF1B1B1F),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                    focusedBorderColor = Color(0xFF7C7CFF),
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White
                ),
                leadingIcon = { Icon(icon, null, tint = Color(0xFF7C7CFF).copy(alpha = 0.7f), modifier = Modifier.size(20.dp)) }
            )
        }
    }
}
