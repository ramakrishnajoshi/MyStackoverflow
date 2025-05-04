package com.example.mystackoverflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystackoverflow.domain.usecase.DetailResult
import com.example.mystackoverflow.domain.usecase.GetQuestionDetailUseCase
import com.example.mystackoverflow.presentation.model.QuestionDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class QuestionDetailViewModel @Inject constructor(
    private val getQuestionDetailUseCase: GetQuestionDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestionDetailUiState())
    val uiState: StateFlow<QuestionDetailUiState> = _uiState

    private val compositeDisposable = CompositeDisposable()

    fun loadQuestion(questionId: Long) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        compositeDisposable.add(
            getQuestionDetailUseCase.execute(questionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    when (result) {
                        is DetailResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    question = result.question,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                        is DetailResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Failed to load question: ${result.throwable.message}"
                                )
                            }
                        }
                    }
                }
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
} 