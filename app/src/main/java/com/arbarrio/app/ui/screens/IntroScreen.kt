package com.arbarrio.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.arbarrio.app.ui.components.ArbarrioLogo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arbarrio.app.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import org.koin.compose.koinInject

class IntroScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authRepository = koinInject<AuthRepository>()
        val scale = remember { Animatable(0.5f) }
        val alpha = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            scale.animateTo(1f, animationSpec = tween(1000))
            alpha.animateTo(1f, animationSpec = tween(1000))
            delay(1500)
            
            val user = authRepository.currentUser.firstOrNull()
            if (user != null) {
                navigator.replaceAll(HomeScreen())
            } else {
                navigator.replaceAll(LoginScreen())
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A2B4C)), // Logo Blue Background
            contentAlignment = Alignment.Center
        ) {
            ArbarrioLogo(
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value),
                size = 120.dp
            )
        }
    }
}
