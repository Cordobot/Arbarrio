package com.arbarrio.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.arbarrio.app.ui.theme.DarkBg
import com.arbarrio.app.data.local.PreferencesManager
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.arbarrio.app.R
import androidx.compose.ui.res.stringResource
import org.koin.compose.koinInject

class SettingsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val preferencesManager: PreferencesManager = koinInject()
        val scope = rememberCoroutineScope()
        
        val isBiometricEnabled by preferencesManager.isBiometricEnabled.collectAsState(initial = false)

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.settings_title), color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = DarkBg
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    SettingsGroup(title = stringResource(R.string.security_section)) {
                        SettingsToggleItem(
                            title = stringResource(R.string.biometric_label), 
                            subtitle = stringResource(R.string.biometric_desc),
                            checked = isBiometricEnabled,
                            onCheckedChange = { 
                                scope.launch {
                                    preferencesManager.saveBiometricSetting(it)
                                }
                            }
                        )
                    }

                    SettingsGroup(title = stringResource(R.string.notifications_section)) {
                        SettingsToggleItem(title = "Nuevos Mensajes", checked = true, onCheckedChange = {})
                        SettingsToggleItem(title = "Alertas de Propiedades", checked = true, onCheckedChange = {})
                    }
                    
                    SettingsGroup(title = "Privacidad") {
                        SettingsItem(title = "Quién puede ver mi perfil", icon = Icons.Default.Visibility)
                        SettingsItem(title = "Bloquear usuarios", icon = Icons.Default.Block)
                    }
                    
                    SettingsGroup(title = stringResource(R.string.app_section)) {
                        var showLanguageDialog by remember { mutableStateOf(false) }
                        var showThemeDialog by remember { mutableStateOf(false) }
                        val currentLangCode by preferencesManager.language.collectAsState(initial = "es")
                        val languages = listOf("Español" to "es", "English" to "en")
                        val selectedLanguage = languages.firstOrNull { it.second == currentLangCode }?.first ?: "Español"
                        var selectedTheme by remember { mutableStateOf("Sistema") }

                        SettingsItem(
                            title = stringResource(R.string.language_label), 
                            icon = Icons.Default.Language, 
                            value = selectedLanguage,
                            onClick = { showLanguageDialog = true }
                        )
                        SettingsItem(
                            title = "Modo Oscuro", 
                            icon = Icons.Default.DarkMode, 
                            value = selectedTheme,
                            onClick = { showThemeDialog = true }
                        )

                        if (showLanguageDialog) {
                            AlertDialog(
                                onDismissRequest = { showLanguageDialog = false },
                                title = { Text("Seleccionar Idioma") },
                                text = {
                                    Column {
                                        languages.forEach { (name, code) ->
                                            TextButton(
                                                onClick = { 
                                                    scope.launch {
                                                        preferencesManager.saveLanguage(code)
                                                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
                                                    }
                                                    showLanguageDialog = false 
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(name, color = if (currentLangCode == code) Color(0xFF7C7CFF) else Color.White)
                                            }
                                        }
                                    }
                                },
                                confirmButton = {},
                                containerColor = Color(0xFF1B1B1F)
                            )
                        }

                        if (showThemeDialog) {
                            AlertDialog(
                                onDismissRequest = { showThemeDialog = false },
                                title = { Text("Seleccionar Tema") },
                                text = {
                                    Column {
                                        listOf("Sistema", "Claro", "Oscuro").forEach { theme ->
                                            TextButton(
                                                onClick = { 
                                                    selectedTheme = theme
                                                    showThemeDialog = false 
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(theme, color = if (selectedTheme == theme) Color(0xFF7C7CFF) else Color.White)
                                            }
                                        }
                                    }
                                },
                                confirmButton = {},
                                containerColor = Color(0xFF1B1B1F)
                            )
                        }
                    }
                    
                    SettingsGroup(title = "Acerca de") {
                        SettingsItem(title = "Términos y Condiciones", icon = Icons.Default.Description)
                        SettingsItem(title = "Política de Privacidad", icon = Icons.Default.Security)
                        SettingsItem(title = "Versión de la App", icon = Icons.Default.Info, value = "1.0.0")
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
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
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF7C7CFF), // Primary Purple
                modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
            )
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1B1B1F).copy(alpha = 0.8f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.1f))
            ) {
                Column {
                    content()
                }
            }
        }
    }

    @Composable
    fun SettingsItem(title: String, icon: ImageVector, value: String? = null, onClick: () -> Unit = {}) {
        Surface(
            onClick = onClick,
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF7C7CFF), modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(16.dp))
                Text(text = title, color = Color.White, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                if (value != null) {
                    Text(text = value, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, 
                    contentDescription = null, 
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    @Composable
    fun SettingsToggleItem(title: String, subtitle: String? = null, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Medium)
                if (subtitle != null) {
                    Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
                }
            }
            Switch(
                checked = checked, 
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF7C7CFF),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFF2D2D33)
                )
            )
        }
    }
}
