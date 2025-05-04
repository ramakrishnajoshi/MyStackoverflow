package com.example.mystackoverflow.domain.mapper

import com.example.mystackoverflow.data.model.QuestionItem
import com.example.mystackoverflow.data.model.Owner
import com.example.mystackoverflow.domain.model.Question
import com.example.mystackoverflow.domain.model.Author

object QuestionMapper {
    fun QuestionItem.toDomain(): Question = Question(
        id = questionId,
        title = title,
        body = body,
        link = link,
        score = score,
        answerCount = answerCount,
        creationDate = creationDate,
        tags = tags,
        author = owner.toDomain(),
        isAnswered = isAnswered
    )

    private fun Owner.toDomain(): Author = Author(
        id = userId,
        name = displayName,
        profileImage = profileImage,
        reputation = reputation
    )
} 