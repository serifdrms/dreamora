package com.isteserif.dreamora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.isteserif.dreamora.ui.screens.DreamScreen
import com.isteserif.dreamora.ui.theme.DreamoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DreamoraTheme {
                DreamScreen()
            }
        }
    }
}