package com.example.mystackoverflow.data.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("items")
    val items: List<QuestionItem>,
    @SerializedName("has_more")
    val hasMore: Boolean,
    @SerializedName("quota_max")
    val quotaMax: Int,
    @SerializedName("quota_remaining")
    val quotaRemaining: Int,
    @SerializedName("backoff")
    val backoff: Int? = null,
    @SerializedName("error_id")
    val errorId: Int? = null,
    @SerializedName("error_message")
    val errorMessage: String? = null,
    @SerializedName("error_name")
    val errorName: String? = null
)

data class QuestionItem(
    @SerializedName("question_id")
    val questionId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("answer_count")
    val answerCount: Int,
    @SerializedName("creation_date")
    val creationDate: Long,
    @SerializedName("body")
    val body: String,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("owner")
    val owner: Owner,
    @SerializedName("is_answered")
    val isAnswered: Boolean
)

data class Owner(
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("profile_image")
    val profileImage: String?,
    @SerializedName("reputation")
    val reputation: Int
) 