package com.example.mystackoverflow.presentation.model

import com.example.mystackoverflow.domain.model.Question

data class SearchUiState(
    val query: String = "",
    val questions: List<Question> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val validationMessage: String? = null
)

data class QuestionDetailUiState(
    val question: Question? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class NetworkStatusUiState(
    val isConnected: Boolean = true,
    val showConnectedMessage: Boolean = false
) 