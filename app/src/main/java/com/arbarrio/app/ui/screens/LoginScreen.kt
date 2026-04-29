package com.arbarrio.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
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
import org.koin.androidx.compose.koinViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class LoginScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<AuthViewModel>()
        
        val savedEmail by viewModel.savedEmail.collectAsState()
        val isRememberMeEnabled by viewModel.isRememberMeEnabled.collectAsState()

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var rememberMe by remember { mutableStateOf(false) }
        var showResetSuccess by remember { mutableStateOf(false) }

        LaunchedEffect(savedEmail) {
            if (savedEmail.isNotBlank()) email = savedEmail
        }
        LaunchedEffect(isRememberMeEnabled) {
            rememberMe = isRememberMeEnabled
        }

        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0E17))) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo Icon in Purple Box
                Surface(
                    modifier = Modifier.size(90.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF7C7CFF)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Arbarrio",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Text(
                    text = "Encuentra arriendo fácilmente",
                    fontSize = 16.sp,
                    color = Color(0xFF7C7CFF),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Email Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Correo electrónico", color = Color(0xFF7C7CFF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("ejemplo@correo.com", color = Color.Gray.copy(alpha = 0.5f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFF1B1B1F),
                            focusedContainerColor = Color(0xFF1B1B1F),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            focusedBorderColor = Color(0xFF7C7CFF),
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        ),
                        trailingIcon = { Icon(Icons.Default.Email, null, tint = Color.Gray.copy(alpha = 0.5f)) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Password Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Contraseña", color = Color(0xFF7C7CFF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("••••••••", color = Color.Gray.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFF1B1B1F),
                            focusedContainerColor = Color(0xFF1B1B1F),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            focusedBorderColor = Color(0xFF7C7CFF),
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        ),
                        trailingIcon = { Icon(Icons.Default.Lock, null, tint = Color.Gray.copy(alpha = 0.5f)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Remember Me & Forgot Password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF7C7CFF))
                        )
                        Text("Recordar", color = Color.Gray, fontSize = 14.sp)
                    }
                    TextButton(onClick = { 
                        viewModel.resetPassword(email) {
                            showResetSuccess = true
                        }
                    }) {
                        Text("¿Olvidaste?", color = Color(0xFF7C7CFF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                // Error Message
                if (error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF442726),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.2f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFFF87171), androidx.compose.foundation.shape.CircleShape))
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = error!!,
                                color = Color(0xFFF87171),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Success Reset Message
                if (showResetSuccess) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF1E3A24),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Enlace de recuperación enviado a tu correo.",
                            color = Color(0xFF4ADE80),
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login Button
                Button(
                    onClick = { 
                        viewModel.saveRememberMe(rememberMe, email)
                        viewModel.signIn(email, password) {
                            navigator.replaceAll(HomeScreen())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C7CFF)),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Ingresar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { navigator.push(RegisterScreen()) }) {
                    Row {
                        Text("¿No tienes cuenta? ", color = Color.Gray)
                        Text("Regístrate", color = Color(0xFF7C7CFF), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Social Section
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f))
                    Text("O continuar con", modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray, fontSize = 12.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SocialButton(
                        text = "Google",
                        imageUri = "google",
                        modifier = Modifier.weight(1f)
                    )
                    SocialButton(
                        text = "Facebook",
                        imageUri = "facebook",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    @Composable
    fun SocialButton(text: String, imageUri: String, modifier: Modifier) {
        OutlinedButton(
            onClick = { },
            modifier = modifier.height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF1B1B1F)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // In a real app we'd use Painter with resource, here using a color box to simulate the look
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(if (imageUri == "google") Color.White else Color(0xFF1877F2), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                   Text(if (imageUri == "google") "G" else "f", color = if (imageUri == "google") Color.Black else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Text(text, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
