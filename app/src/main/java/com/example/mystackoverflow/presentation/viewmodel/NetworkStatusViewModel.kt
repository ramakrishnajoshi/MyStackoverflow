package com.example.mystackoverflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystackoverflow.domain.usecase.NetworkConnectivityUseCase
import com.example.mystackoverflow.presentation.model.NetworkStatusUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkStatusViewModel @Inject constructor(
    private val networkConnectivityUseCase: NetworkConnectivityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NetworkStatusUiState())
    val uiState: StateFlow<NetworkStatusUiState> = _uiState

    private val compositeDisposable = CompositeDisposable()
    private var lastConnectionState: Boolean? = null

    init {
        observeNetworkStatus()
    }

    private fun observeNetworkStatus() {
        compositeDisposable.add(
            networkConnectivityUseCase.observe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isConnected ->
                    // Only update if the connection state has actually changed
                    if (lastConnectionState != isConnected) {
                        lastConnectionState = isConnected
                        _uiState.update { 
                            it.copy(
                                isConnected = isConnected,
                                showConnectedMessage = isConnected
                            )
                        }
                        if (isConnected) {
                            startConnectedMessageTimer()
                        }
                    }
                }
        )
    }

    private fun startConnectedMessageTimer() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            _uiState.update { it.copy(showConnectedMessage = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
        networkConnectivityUseCase.cleanup()
    }
} 