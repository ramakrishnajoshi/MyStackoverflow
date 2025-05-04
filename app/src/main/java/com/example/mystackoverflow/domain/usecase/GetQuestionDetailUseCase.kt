package com.example.mystackoverflow.domain.usecase

import com.example.mystackoverflow.data.repository.QuestionRepository
import com.example.mystackoverflow.domain.mapper.QuestionMapper.toDomain
import com.example.mystackoverflow.domain.model.Question
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetQuestionDetailUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    fun execute(questionId: Long): Single<DetailResult> {
        return repository.getQuestion(questionId)
            .map<DetailResult> { question -> DetailResult.Success(question.toDomain()) }
            .onErrorReturn { DetailResult.Error(it) }
    }
}

sealed class DetailResult {
    data class Success(val question: Question) : DetailResult()
    data class Error(val throwable: Throwable) : DetailResult()
} 