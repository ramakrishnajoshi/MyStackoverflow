package com.example.mystackoverflow.data.repository

import com.example.mystackoverflow.data.api.StackOverflowApi
import com.example.mystackoverflow.data.model.Owner
import com.example.mystackoverflow.data.model.QuestionItem
import com.example.mystackoverflow.data.model.SearchResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class QuestionRepositoryTest {
    private lateinit var api: StackOverflowApi
    private lateinit var repository: QuestionRepository

    private val mockQuestionItem = QuestionItem(
        questionId = 1L,
        title = "Test Question",
        body = "Test Body",
        link = "https://stackoverflow.com/q/1",
        score = 10,
        answerCount = 2,
        creationDate = 1234567890L,
        tags = listOf("android", "kotlin"),
        owner = Owner(
            userId = 1L,
            displayName = "Test Author",
            profileImage = "https://example.com/profile.jpg",
            reputation = 1000
        ),
        isAnswered = false
    )

    @Before
    fun setup() {
        api = mockk()
        repository = QuestionRepositoryImpl(api)
    }

    @Test
    fun `searchQuestions with valid query returns list of questions`() {
        // Given
        val query = "android"
        val response = SearchResponse(
            items = listOf(mockQuestionItem),
            hasMore = false,
            quotaMax = 300,
            quotaRemaining = 299
        )
        every { api.searchQuestions(query = query, page = 1) } returns Single.just(response)

        // When
        val result = repository.searchQuestions(query).blockingGet()

        // Then
        assertEquals(1, result.size)
        assertEquals(mockQuestionItem, result.first())
        verify { api.searchQuestions(query = query, page = 1) }
    }

    @Test
    fun `searchQuestions with API error throws exception`() {
        // Given
        val query = "android"
        val exception = RuntimeException("Network error")
        every { api.searchQuestions(query = query, page = 1) } returns Single.error(exception)

        // When/Then
        val thrown = assertFailsWith<RuntimeException> {
            repository.searchQuestions(query).blockingGet()
        }
        assertEquals("Network error", thrown.message)
    }

    @Test
    fun `searchQuestions with error ID in response throws StackOverflowApiException`() {
        // Given
        val query = "android"
        val response = SearchResponse(
            items = emptyList(),
            hasMore = false,
            quotaMax = 300,
            quotaRemaining = 0,
            errorId = 502,
            errorMessage = "Too many requests",
            errorName = "throttle_violation"
        )
        every { api.searchQuestions(query = query, page = 1) } returns Single.just(response)

        try {
            repository.searchQuestions(query).blockingGet()
            throw AssertionError("Expected StackOverflowApiException to be thrown")
        } catch (e: RuntimeException) {
            val cause = e.cause
            assertTrue(cause is StackOverflowApiException, "Expected StackOverflowApiException but was ${cause?.javaClass?.simpleName}")
            cause as StackOverflowApiException
            assertEquals("Too many requests", cause.message)
            assertEquals(502, cause.errorId)
            assertEquals("throttle_violation", cause.errorName)
        }
    }

    @Test
    fun `searchQuestions with empty results returns empty list`() {
        // Given
        val query = "nonexistentquery123456"
        val response = SearchResponse(
            items = emptyList(),
            hasMore = false,
            quotaMax = 300,
            quotaRemaining = 299
        )
        every { api.searchQuestions(query = query, page = 1) } returns Single.just(response)

        // When
        val result = repository.searchQuestions(query).blockingGet()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `searchQuestions with pagination returns correct page`() {
        // Given
        val query = "android"
        val page = 2
        val response = SearchResponse(
            items = listOf(mockQuestionItem),
            hasMore = true,
            quotaMax = 300,
            quotaRemaining = 298
        )
        every { api.searchQuestions(query = query, page = page) } returns Single.just(response)

        // When
        val result = repository.searchQuestions(query, page).blockingGet()

        // Then
        assertEquals(1, result.size)
        verify { api.searchQuestions(query = query, page = page) }
    }

    @Test
    fun `getQuestion with valid ID returns question`() {
        // Given
        val questionId = 1L
        val response = SearchResponse(
            items = listOf(mockQuestionItem),
            hasMore = false,
            quotaMax = 300,
            quotaRemaining = 299
        )
        every { api.getQuestion(questionId = questionId) } returns Single.just(response)

        // When
        val result = repository.getQuestion(questionId).blockingGet()

        // Then
        assertEquals(mockQuestionItem, result)
    }

    @Test
    fun `getQuestion with invalid ID throws NoSuchElementException`() {
        // Given
        val questionId = 999L
        val response = SearchResponse(
            items = emptyList(),
            hasMore = false,
            quotaMax = 300,
            quotaRemaining = 299
        )
        every { api.getQuestion(questionId = questionId) } returns Single.just(response)

        // When/Then
        assertFailsWith<NoSuchElementException> {
            repository.getQuestion(questionId).blockingGet()
        }
    }

    @Test
    fun `getQuestion with API error throws exception`() {
        // Given
        val questionId = 1L
        val exception = RuntimeException("Network error")
        every { api.getQuestion(questionId = questionId) } returns Single.error(exception)

        // When/Then
        val thrown = assertFailsWith<RuntimeException> {
            repository.getQuestion(questionId).blockingGet()
        }
        assertEquals("Network error", thrown.message)
    }

    @Test
    fun `getQuestion with error ID in response throws StackOverflowApiException`() {
        // Given
        val questionId = 1L
        val response = SearchResponse(
            items = emptyList(),
            hasMore = false,
            quotaMax = 300,
            quotaRemaining = 0,
            errorId = 404,
            errorMessage = "Question not found",
            errorName = "not_found"
        )
        every { api.getQuestion(questionId = questionId) } returns Single.just(response)

        try {
            repository.getQuestion(questionId).blockingGet()
            throw AssertionError("Expected StackOverflowApiException to be thrown")
        } catch (e: RuntimeException) {
            val cause = e.cause
            assertTrue(cause is StackOverflowApiException, "Expected StackOverflowApiException but was ${cause?.javaClass?.simpleName}")
            cause as StackOverflowApiException
            assertEquals("Question not found", cause.message)
            assertEquals(404, cause.errorId)
            assertEquals("not_found", cause.errorName)
        }
    }
} 