package com.arbarrio.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import cafe.adriel.voyager.navigator.Navigator
import com.arbarrio.app.ui.screens.IntroScreen
import com.arbarrio.app.ui.theme.ArbarrioTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArbarrioTheme {
                Navigator(IntroScreen())
            }
        }
    }
}
