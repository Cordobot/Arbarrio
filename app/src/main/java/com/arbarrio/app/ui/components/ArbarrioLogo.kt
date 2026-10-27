package com.arbarrio.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arbarrio.app.ui.theme.LogoMint
import com.arbarrio.app.ui.theme.LogoOrange

@Composable
fun ArbarrioLogo(
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    size: Dp = 120.dp
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // House and Key Icon
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = size.toPx() * 0.04f
            
            // 1. House Path
            val housePath = Path().apply {
                // Roof top
                moveTo(size.toPx() * 0.5f, size.toPx() * 0.1f)
                // Left roof corner
                lineTo(size.toPx() * 0.15f, size.toPx() * 0.45f)
                // Left wall top
                lineTo(size.toPx() * 0.25f, size.toPx() * 0.45f)
                // Left wall bottom
                lineTo(size.toPx() * 0.25f, size.toPx() * 0.9f)
                // Right wall bottom
                lineTo(size.toPx() * 0.75f, size.toPx() * 0.9f)
                // Right wall top
                lineTo(size.toPx() * 0.75f, size.toPx() * 0.45f)
                // Right roof corner
                lineTo(size.toPx() * 0.85f, size.toPx() * 0.45f)
                // Back to roof top
                close()
            }
            
            drawPath(
                path = housePath,
                color = LogoMint,
                style = Stroke(width = strokeWidth)
            )

            // Door outline (simplified)
            drawRoundRect(
                color = LogoMint,
                topLeft = Offset(size.toPx() * 0.42f, size.toPx() * 0.65f),
                size = Size(size.toPx() * 0.16f, size.toPx() * 0.25f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
                style = Stroke(width = strokeWidth)
            )

            // 2. Key Icon (Yellow/Orange)
            val keySize = size.toPx() * 0.15f
            val keyX = size.toPx() * 0.65f
            val keyY = size.toPx() * 0.45f
            
            // Key Circle
            drawCircle(
                color = LogoOrange,
                radius = keySize,
                center = Offset(keyX, keyY),
                style = Stroke(width = strokeWidth * 1.5f)
            )
            
            // Key Stem
            drawLine(
                color = LogoOrange,
                start = Offset(keyX + keySize, keyY),
                end = Offset(keyX + keySize * 2.5f, keyY),
                strokeWidth = strokeWidth * 1.5f
            )
            
            // Key Teeth
            drawLine(
                color = LogoOrange,
                start = Offset(keyX + keySize * 1.8f, keyY),
                end = Offset(keyX + keySize * 1.8f, keyY + keySize * 0.8f),
                strokeWidth = strokeWidth * 1.5f
            )
            drawLine(
                color = LogoOrange,
                start = Offset(keyX + keySize * 2.3f, keyY),
                end = Offset(keyX + keySize * 2.3f, keyY + keySize * 0.8f),
                strokeWidth = strokeWidth * 1.5f
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "ARBARRIO",
                color = Color.White,
                fontSize = (size.value * 0.3f).sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            
            Text(
                text = "ARRENDAMIENTOS",
                color = LogoMint,
                fontSize = (size.value * 0.1f).sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 6.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            // Horizontal line below ARRENDAMIENTOS
            Canvas(modifier = Modifier.width(size * 1.5f).height(10.dp).padding(top = 8.dp)) {
                drawLine(
                    color = LogoMint,
                    start = Offset(0f, 0f),
                    end = Offset(size.toPx() * 1.5f, 0f),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }
}
