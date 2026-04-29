package com.arbarrio.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arbarrio.app.ui.components.AnimatedBackground

class SettingsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Configuración") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { padding ->
            AnimatedBackground {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    SettingsGroup(title = "Notificaciones") {
                        SettingsToggleItem(title = "Nuevos Mensajes", initialValue = true)
                        SettingsToggleItem(title = "Alertas de Propiedades", initialValue = true)
                    }
                    
                    SettingsGroup(title = "Privacidad") {
                        SettingsItem(title = "Quién puede ver mi perfil", icon = Icons.Default.Visibility)
                        SettingsItem(title = "Bloquear usuarios", icon = Icons.Default.Block)
                    }
                    
                    SettingsGroup(title = "App") {
                        SettingsItem(title = "Idioma", icon = Icons.Default.Language, value = "Español")
                        SettingsItem(title = "Modo Oscuro", icon = Icons.Default.DarkMode, value = "Sistema")
                    }
                    
                    SettingsGroup(title = "Acerca de") {
                        SettingsItem(title = "Términos y Condiciones", icon = Icons.Default.Description)
                        SettingsItem(title = "Política de Privacidad", icon = Icons.Default.Security)
                        SettingsItem(title = "Versión de la App", icon = Icons.Default.Info, value = "1.0.0")
                    }
                }
            }
        }
    }

    @Composable
    fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                tonalElevation = 2.dp
            ) {
                Column {
                    content()
                }
            }
        }
    }

    @Composable
    fun SettingsItem(title: String, icon: ImageVector, value: String? = null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.width(16.dp))
            Text(text = title, modifier = Modifier.weight(1f))
            if (value != null) {
                Text(text = value, color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
            }
            // Use KeyboardArrowRight which is standard
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, 
                contentDescription = null, 
                tint = Color.Gray
            )
        }
    }

    @Composable
    fun SettingsToggleItem(title: String, initialValue: Boolean) {
        var checked by remember { mutableStateOf(initialValue) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, modifier = Modifier.weight(1f))
            Switch(checked = checked, onCheckedChange = { checked = it })
        }
    }
}
