package com.isteserif.dreamora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.isteserif.dreamora.domain.model.Dream
import com.isteserif.dreamora.ui.screens.DreamDetailScreen
import com.isteserif.dreamora.ui.screens.DreamScreen
import com.isteserif.dreamora.ui.screens.HistoryScreen
import com.isteserif.dreamora.ui.theme.DreamoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DreamoraTheme {
                var showHistory by remember { mutableStateOf(false) }
                var selectedDream by remember { mutableStateOf<Dream?>(null) }

                // Detay ekranındayken geri → geçmiş ekranına
                BackHandler(enabled = selectedDream != null) {
                    selectedDream = null
                }

                // Geçmiş ekranındayken geri → ana ekrana
                BackHandler(enabled = showHistory && selectedDream == null) {
                    showHistory = false
                }

                when {
                    selectedDream != null -> {
                        DreamDetailScreen(
                            dream = selectedDream!!,
                            onBackClick = { selectedDream = null }
                        )
                    }
                    showHistory -> {
                        HistoryScreen(
                            onBackClick = { showHistory = false },
                            onDreamClick = { dream -> selectedDream = dream }
                        )
                    }
                    else -> {
                        DreamScreen(onHistoryClick = { showHistory = true })
                    }
                }
            }
        }
    }
}