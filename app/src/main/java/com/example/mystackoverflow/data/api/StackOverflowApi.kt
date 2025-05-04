package com.example.mystackoverflow.data.api

import com.example.mystackoverflow.data.api.ApiConstants
import com.example.mystackoverflow.data.model.SearchResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StackOverflowApi {
    @GET("search/advanced")
    fun searchQuestions(
        @Query("q") query: String,
        @Query("site") site: String = "stackoverflow",
        @Query("key") key: String = ApiConstants.API_KEY,
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "relevance",
        @Query("filter") filter: String = "withbody", // Include the question body in response
        @Query("pagesize") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): Single<SearchResponse>

    @GET("questions/{questionId}")
    fun getQuestion(
        @Path("questionId") questionId: Long,
        @Query("site") site: String = "stackoverflow",
        @Query("key") key: String = ApiConstants.API_KEY,
        @Query("filter") filter: String = "withbody"
    ): Single<SearchResponse>
} 