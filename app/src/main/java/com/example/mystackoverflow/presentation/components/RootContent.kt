package com.example.mystackoverflow.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mystackoverflow.presentation.viewmodel.NetworkStatusViewModel

@Composable
fun RootContent(
    networkStatusViewModel: NetworkStatusViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val networkState by networkStatusViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize()
    ) {
        AnimatedVisibility(
            visible = !networkState.isConnected || networkState.showConnectedMessage,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            NetworkStatusBanner(
                isConnected = networkState.isConnected,
                showConnectedMessage = networkState.showConnectedMessage
            )
        }
        
        content()
    }
} 