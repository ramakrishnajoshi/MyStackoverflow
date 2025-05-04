package com.example.mystackoverflow.di

import android.content.Context
import com.example.mystackoverflow.data.repository.QuestionRepository
import com.example.mystackoverflow.domain.usecase.GetQuestionDetailUseCase
import com.example.mystackoverflow.domain.usecase.NetworkConnectivityUseCase
import com.example.mystackoverflow.domain.usecase.SearchQuestionsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideSearchQuestionsUseCase(
        repository: QuestionRepository
    ): SearchQuestionsUseCase = SearchQuestionsUseCase(repository)

    @Provides
    fun provideGetQuestionDetailUseCase(
        repository: QuestionRepository
    ): GetQuestionDetailUseCase = GetQuestionDetailUseCase(repository)

    @Provides
    @Singleton
    fun provideNetworkConnectivityUseCase(
        @ApplicationContext context: Context
    ): NetworkConnectivityUseCase = NetworkConnectivityUseCase(context)
} 