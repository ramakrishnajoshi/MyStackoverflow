package com.example.mystackoverflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystackoverflow.domain.usecase.SearchQuestionsUseCase
import com.example.mystackoverflow.domain.usecase.SearchResult
import com.example.mystackoverflow.presentation.model.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchQuestionsUseCase: SearchQuestionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    private val compositeDisposable = CompositeDisposable()
    private val querySubject = BehaviorSubject.create<String>()

    init {
        setupSearchDebounce()
        performSearch("")
    }

    private fun setupSearchDebounce() {
        compositeDisposable.add(
            querySubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { query -> performSearch(query) }
        )
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        querySubject.onNext(query)
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            _uiState.update { 
                it.copy(
                    questions = emptyList(),
                    validationMessage = "Enter three or more characters",
                    isLoading = false,
                    error = null
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, validationMessage = null) }

        compositeDisposable.add(
            searchQuestionsUseCase.execute(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    when (result) {
                        is SearchResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    questions = result.questions,
                                    isLoading = false,
                                    error = null,
                                    validationMessage = null
                                )
                            }
                        }
                        is SearchResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Failed to load questions: ${result.throwable.message}",
                                    validationMessage = null
                                )
                            }
                        }
                        is SearchResult.ValidationError -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    validationMessage = result.message,
                                    error = null
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