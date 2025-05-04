package com.example.mystackoverflow.domain.usecase

import com.example.mystackoverflow.data.repository.QuestionRepository
import com.example.mystackoverflow.domain.mapper.QuestionMapper.toDomain
import com.example.mystackoverflow.domain.model.Question
import com.example.mystackoverflow.domain.validation.SearchValidator
import com.example.mystackoverflow.domain.validation.SearchValidationResult
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class SearchQuestionsUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    fun execute(query: String, page: Int = 1): Single<SearchResult> {
        return when (val validationResult = SearchValidator.validate(query)) {
            is SearchValidationResult.Valid -> {
                repository.searchQuestions(query, page)
                    .map<SearchResult> { questions -> 
                        SearchResult.Success(questions.map { it.toDomain() })
                    }
                    .onErrorReturn { SearchResult.Error(it) }
            }
            is SearchValidationResult.Invalid -> {
                Single.just<SearchResult>(SearchResult.ValidationError(validationResult.message))
            }
        }
    }
}

sealed class SearchResult {
    data class Success(val questions: List<Question>) : SearchResult()
    data class Error(val throwable: Throwable) : SearchResult()
    data class ValidationError(val message: String) : SearchResult()
} 