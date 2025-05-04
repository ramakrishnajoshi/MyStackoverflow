package com.example.mystackoverflow.domain.model

data class Question(
    val id: Long,
    val title: String,
    val body: String,
    val link: String,
    val score: Int,
    val answerCount: Int,
    val creationDate: Long,
    val tags: List<String>,
    val author: Author,
    val isAnswered: Boolean
)

data class Author(
    val id: Long,
    val name: String,
    val profileImage: String?,
    val reputation: Int
) 