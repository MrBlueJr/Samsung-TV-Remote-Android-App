package com.vibecode.tvremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.vibecode.tvremote.ui.screens.DiscoveryScreen
import com.vibecode.tvremote.ui.screens.RemoteScreen
import com.vibecode.tvremote.ui.theme.TvRemoteTheme

class MainActivity : ComponentActivity() {
    
    enum class AppScreen {
        DISCOVERY,
        REMOTE
    }

    private val viewModel: RemoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            TvRemoteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    var currentScreen by remember { mutableStateOf(AppScreen.DISCOVERY) }

                    LaunchedEffect(Unit) {
                        if (viewModel.currentTvIp != null) {
                            currentScreen = AppScreen.REMOTE
                        }
                    }

                    Crossfade(
                        targetState = currentScreen,
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            AppScreen.DISCOVERY -> {
                                DiscoveryScreen(
                                    viewModel = viewModel,
                                    onNavigateToRemote = {
                                        currentScreen = AppScreen.REMOTE
                                    }
                                )
                            }
                            AppScreen.REMOTE -> {
                                RemoteScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        currentScreen = AppScreen.DISCOVERY
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
