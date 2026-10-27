package com.arbarrio.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.arbarrio.app.R
import androidx.compose.ui.res.stringResource
import com.arbarrio.app.domain.model.User
import coil.compose.AsyncImage
import com.arbarrio.app.domain.model.Property
import java.text.NumberFormat
import java.util.*

import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arbarrio.app.ui.theme.LogoutRed
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat


class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<HomeViewModel>()
        val properties by viewModel.properties.collectAsState()
        val categories by viewModel.categories.collectAsState()
        val selectedCategory by viewModel.selectedCategory.collectAsState()
        val user by viewModel.currentUser.collectAsState()
        val currentCity by viewModel.currentCity.collectAsState()
        val favoriteCount by viewModel.favoriteCount.collectAsState()
        val myPropertiesCount by viewModel.myPropertiesCount.collectAsState()

        val context = LocalContext.current
        val locationPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                viewModel.updateLocation()
            }
        }

        LaunchedEffect(Unit) {
            val fineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            val coarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            
            if (fineLocationPermission == PackageManager.PERMISSION_GRANTED || coarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                viewModel.updateLocation()
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var showLogoutDialog by remember { mutableStateOf(false) }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(stringResource(R.string.logout_title)) },
                text = { Text(stringResource(R.string.logout_message)) },
                confirmButton = {
                    TextButton(onClick = { 
                        showLogoutDialog = false
                        navigator.replaceAll(LoginScreen())
                    }) {
                        Text(stringResource(R.string.logout_confirm), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = Color(0xFF0F0F12),
                    drawerContentColor = Color.White,
                    modifier = Modifier.width(320.dp),
                    windowInsets = WindowInsets(0)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(Modifier.height(48.dp))
                        
                        // User Profile Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF1B1B1F),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(60.dp),
                                    shape = CircleShape,
                                    color = Color(0xFF6366F1)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        val initials = "${user?.name?.firstOrNull() ?: "A"}${user?.lastName?.firstOrNull() ?: "A"}".uppercase()
                                        Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    }
                                }
                                
                                Spacer(Modifier.width(16.dp))
                                
                                Column {
                                    Text(
                                        text = "${user?.name ?: "Adrian"} ${user?.lastName ?: "Alvarez"}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = stringResource(R.string.app_name) + " • Medellín",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Surface(
                                        color = Color(0xFF059669).copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.size(6.dp).background(Color(0xFF10B981), CircleShape))
                                            Spacer(Modifier.width(6.dp))
                                            Text(stringResource(R.string.verified_label), fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        
                        // Quick Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DrawerStatBox(count = myPropertiesCount, label = stringResource(R.string.stat_properties), modifier = Modifier.weight(1f))
                            DrawerStatBox(count = favoriteCount, label = stringResource(R.string.stat_favorites), modifier = Modifier.weight(1f))
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        
                        // Section: PRINCIPAL
                        DrawerSectionHeader("PRINCIPAL")
                        DrawerItem(icon = Icons.Default.Home, label = stringResource(R.string.menu_home), selected = true) {
                            scope.launch { drawerState.close() }
                        }
                        DrawerItem(
                            icon = Icons.Default.LocationOn,
                            label = stringResource(R.string.menu_my_properties),
                            selected = false,
                            badge = if (myPropertiesCount == "0") null else myPropertiesCount
                        ) {
                            scope.launch { drawerState.close() }
                            navigator.push(MyPropertiesScreen())
                        }
                        DrawerItem(
                            icon = Icons.Default.Favorite,
                            label = stringResource(R.string.menu_favorites),
                            selected = false,
                            badge = if (favoriteCount == "0") null else favoriteCount
                        ) {
                            scope.launch { drawerState.close() }
                            navigator.push(FavoritesScreen())
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        
                        // Section: CUENTA
                        DrawerSectionHeader(stringResource(R.string.security_section).uppercase())
                        DrawerItem(icon = Icons.Default.Person, label = stringResource(R.string.menu_profile)) {
                            scope.launch { drawerState.close() }
                            navigator.push(EditProfileScreen())
                        }
                        DrawerItem(icon = Icons.Default.Settings, label = stringResource(R.string.menu_settings)) {
                            scope.launch { drawerState.close() }
                            navigator.push(SettingsScreen())
                        }
                        DrawerItem(icon = Icons.AutoMirrored.Filled.Help, label = stringResource(R.string.menu_help)) {}
                        
                        Spacer(Modifier.weight(1f))
                        Spacer(Modifier.height(24.dp))
                        
                        // Logout Button
                        Button(
                            onClick = { 
                                scope.launch { drawerState.close() }
                                showLogoutDialog = true 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.Logout, null, tint = LogoutRed)
                                Spacer(Modifier.width(12.dp))
                                Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        ) {
            Scaffold(
                containerColor = Color(0xFF0F0F12),
                topBar = {
                    HomeTopBar(
                        currentCity = currentCity,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { navigator.push(AddPropertyScreen()) },
                        containerColor = Color(0xFF6366F1),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir Propiedad", modifier = Modifier.size(28.dp))
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Spacer(Modifier.height(8.dp))
                    
                    SearchBar()
                    
                    Spacer(Modifier.height(24.dp))
                    
                    CategorySection(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.selectCategory(it) }
                    )

                    Spacer(Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(properties) { property ->
                            PropertyCard(
                                property = property,
                                onFavoriteClick = { viewModel.toggleFavorite(property.id) },
                                onClick = { navigator.push(PropertyDetailScreen(property.id)) }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HomeTopBar(currentCity: String, onMenuClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF1B1B1F), RoundedCornerShape(14.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Ubicación actual",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = currentCity,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            
            // Notification Bell or Profile initial as a secondary action
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchBar() {
        var text by remember { mutableStateOf("") }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            color = Color(0xFF1B1B1F),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(stringResource(R.string.search_hint), color = Color.Gray, fontSize = 15.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF818CF8)) },
                trailingIcon = { 
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Tune, contentDescription = "Filtros", tint = Color.Gray)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }
    }

    @Composable
    fun CategorySection(
        categories: List<String>,
        selectedCategory: String,
        onCategorySelected: (String) -> Unit
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                Surface(
                    onClick = { onCategorySelected(category) },
                    color = if (isSelected) Color(0xFF6366F1) else Color(0xFF1B1B1F),
                    shape = RoundedCornerShape(16.dp),
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Text(
                        text = category,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color.Gray
                    )
                }
            }
        }
    }

    @Composable
    fun PropertyCard(
        property: Property,
        onFavoriteClick: () -> Unit,
        onClick: () -> Unit,
        onDeleteClick: (() -> Unit)? = null
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable { onClick() },
            color = Color(0xFF1B1B1F),
            shape = RoundedCornerShape(32.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    AsyncImage(
                        model = property.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Favorite Button
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (property.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (property.isFavorite) Color.Red else Color.White
                        )
                    }

                    // Delete Button (if provided)
                    if (onDeleteClick != null) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.Red
                            )
                        }
                    }

                    // Category Badge
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        color = Color(0xFF6366F1),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = property.category,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO")).format(property.price),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "/ mes",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = property.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF818CF8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = property.location,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PropertyDetailItem(icon = Icons.Default.Hotel, value = "${property.bedrooms} Hab")
                        PropertyDetailItem(icon = Icons.Default.Bathtub, value = "${property.bathrooms} Baños")
                        PropertyDetailItem(icon = Icons.Default.Straighten, value = "${property.area} m²")
                    }
                }
            }
        }
    }

    @Composable
    fun PropertyDetailItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }

    @Composable
    fun DrawerStatBox(count: String, label: String, modifier: Modifier = Modifier) {
        Surface(
            modifier = modifier,
            color = Color(0xFF1B1B1F).copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = count, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = label, fontSize = 10.sp, color = Color.Gray)
            }
        }
    }

    @Composable
    fun DrawerSectionHeader(title: String) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 12.dp),
            letterSpacing = 1.sp
        )
    }

    @Composable
    fun DrawerItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        label: String,
        selected: Boolean = false,
        badge: String? = null,
        onClick: () -> Unit
    ) {
        Surface(
            onClick = onClick,
            color = if (selected) Color(0xFF6366F1).copy(alpha = 0.1f) else Color.Transparent,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = if (selected) Color(0xFF6366F1).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = if (selected) Color(0xFF818CF8) else Color.Gray
                    )
                }
                
                Spacer(Modifier.width(16.dp))
                
                Text(
                    text = label,
                    fontSize = 15.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) Color.White else Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                
                if (badge != null) {
                    Surface(
                        color = if (selected) Color(0xFF6366F1) else Color(0xFF3730A3),
                        shape = CircleShape
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
