package com.example.mystackoverflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.mystackoverflow.presentation.components.RootContent
import com.example.mystackoverflow.presentation.navigation.AppNavigation
import com.example.mystackoverflow.presentation.navigation.Screen
import com.example.mystackoverflow.presentation.screens.detail.QuestionDetailScreen
import com.example.mystackoverflow.presentation.screens.search.SearchScreen
import com.example.mystackoverflow.ui.theme.StackOverFlowAssignmentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StackOverFlowAssignmentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootContent { 
                        val navController = rememberNavController()

                        AppNavigation(
                            navController = navController,
                            searchScreen = {
                                SearchScreen(
                                    onQuestionClick = { questionId ->
                                        navController.navigate(
                                            Screen.QuestionDetail.createRoute(questionId)
                                        )
                                    }
                                )
                            },
                            questionDetailScreen = { questionId ->
                                QuestionDetailScreen(
                                    questionId = questionId,
                                    onBackClick = {
                                        navController.navigateUp()
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}