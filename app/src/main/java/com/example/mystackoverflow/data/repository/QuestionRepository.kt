package com.example.mystackoverflow.data.repository

import com.example.mystackoverflow.data.api.StackOverflowApi
import com.example.mystackoverflow.data.model.QuestionItem
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

interface QuestionRepository {
    fun searchQuestions(query: String, page: Int = 1): Single<List<QuestionItem>>
    fun getQuestion(questionId: Long): Single<QuestionItem>
}

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val api: StackOverflowApi
) : QuestionRepository {

    override fun searchQuestions(query: String, page: Int): Single<List<QuestionItem>> {
        return api.searchQuestions(query = query, page = page)
            .map { response ->
                if (response.errorId != null) {
                    throw StackOverflowApiException(
                        response.errorMessage ?: "Unknown error",
                        response.errorId,
                        response.errorName
                    )
                }
                response.items
            }
    }

    override fun getQuestion(questionId: Long): Single<QuestionItem> {
        return api.getQuestion(questionId = questionId)
            .map { response ->
                if (response.errorId != null) {
                    throw StackOverflowApiException(
                        response.errorMessage ?: "Unknown error",
                        response.errorId,
                        response.errorName
                    )
                }
                response.items.firstOrNull() 
                    ?: throw NoSuchElementException("Question not found")
            }
    }
}

class StackOverflowApiException(
    message: String,
    val errorId: Int,
    val errorName: String?
) : Exception(message) 