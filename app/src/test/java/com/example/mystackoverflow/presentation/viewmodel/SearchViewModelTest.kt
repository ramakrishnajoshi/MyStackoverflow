package com.example.mystackoverflow.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mystackoverflow.domain.model.Author
import com.example.mystackoverflow.domain.model.Question
import com.example.mystackoverflow.domain.usecase.SearchQuestionsUseCase
import com.example.mystackoverflow.domain.usecase.SearchResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.TestScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var searchQuestionsUseCase: SearchQuestionsUseCase
    private lateinit var viewModel: SearchViewModel
    private lateinit var testScheduler: TestScheduler
    private val testDispatcher = StandardTestDispatcher()

    private val mockQuestion = Question(
        id = 1L,
        title = "Test Question",
        body = "Test question body",
        link = "https://stackoverflow.com/q/1",
        score = 10,
        answerCount = 2,
        creationDate = 1234567890L,
        tags = listOf("android", "kotlin"),
        author = Author(
            id = 1L,
            name = "Test Author",
            profileImage = "https://example.com/profile.jpg",
            reputation = 1000
        ),
        isAnswered = false
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchQuestionsUseCase = mockk()
        testScheduler = TestScheduler()
        
        // Configure RxJava to use TestScheduler
        RxJavaPlugins.setIoSchedulerHandler { testScheduler }
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
        RxJavaPlugins.setNewThreadSchedulerHandler { testScheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        
        viewModel = SearchViewModel(searchQuestionsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun `initial state should have empty query and validation message`() {
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertEquals("Enter three or more characters", state.validationMessage)
        assertNull(state.error)
        assertEquals(emptyList(), state.questions)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `when query is empty, show enter three or more characters message`() {
        viewModel.onQueryChanged("")
        advanceTimeBy(300)
        
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertEquals("Enter three or more characters", state.validationMessage)
        assertEquals(emptyList(), state.questions)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `when query has one character, show two more characters required message`() {
        every { searchQuestionsUseCase.execute("a") } returns Single.just(
            SearchResult.ValidationError("Two more characters required")
        )

        viewModel.onQueryChanged("a")
        advanceTimeBy(300)

        val state = viewModel.uiState.value
        assertEquals("a", state.query)
        assertEquals("Two more characters required", state.validationMessage)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `when query has two characters, show one more character required message`() {
        every { searchQuestionsUseCase.execute("ab") } returns Single.just(
            SearchResult.ValidationError("One more character required")
        )

        viewModel.onQueryChanged("ab")
        advanceTimeBy(300)

        val state = viewModel.uiState.value
        assertEquals("ab", state.query)
        assertEquals("One more character required", state.validationMessage)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `when query has three or more characters, trigger search`() {
        val query = "test"
        val successResult = SearchResult.Success(listOf(mockQuestion))
        every { searchQuestionsUseCase.execute(query) } returns Single.just(successResult)

        viewModel.onQueryChanged(query)
        advanceTimeBy(300)

        verify { searchQuestionsUseCase.execute(query) }
        val state = viewModel.uiState.value
        assertEquals(query, state.query)
        assertEquals(listOf(mockQuestion), state.questions)
        assertNull(state.validationMessage)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `when search fails, show error message`() {
        val query = "test"
        val errorMessage = "Network error"
        val error = SearchResult.Error(RuntimeException(errorMessage))
        every { searchQuestionsUseCase.execute(query) } returns Single.just(error)

        viewModel.onQueryChanged(query)
        advanceTimeBy(300)

        val state = viewModel.uiState.value
        assertEquals(query, state.query)
        assertEquals("Failed to load questions: $errorMessage", state.error)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `when multiple rapid queries, only last query should trigger search`() {
        val finalQuery = "final"
        every { searchQuestionsUseCase.execute(any()) } returns Single.just(SearchResult.Success(emptyList()))

        viewModel.onQueryChanged("first")
        viewModel.onQueryChanged("second")
        viewModel.onQueryChanged(finalQuery)
        
        // Advance time less than debounce period
        advanceTimeBy(200)
        
        // No search should be triggered yet
        verify(exactly = 0) { searchQuestionsUseCase.execute(any()) }

        // Advance time to complete debounce period
        advanceTimeBy(100)
        
        // Only the last query should trigger search
        verify(exactly = 1) { searchQuestionsUseCase.execute(finalQuery) }
    }

    @Test
    fun `when same query entered multiple times, search only once`() {
        val query = "test"
        every { searchQuestionsUseCase.execute(query) } returns Single.just(SearchResult.Success(emptyList()))

        viewModel.onQueryChanged(query)
        viewModel.onQueryChanged(query)
        viewModel.onQueryChanged(query)
        
        advanceTimeBy(300)
        
        // Due to distinctUntilChanged(), only one search should be triggered
        verify(exactly = 1) { searchQuestionsUseCase.execute(query) }
    }

    private fun advanceTimeBy(milliseconds: Long) {
        testScheduler.advanceTimeBy(milliseconds, TimeUnit.MILLISECONDS)
        testDispatcher.scheduler.advanceTimeBy(milliseconds)
    }
} 