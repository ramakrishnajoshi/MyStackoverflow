package com.example.mystackoverflow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Search : Screen("search")
    object QuestionDetail : Screen("question/{questionId}") {
        fun createRoute(questionId: Long) = "question/$questionId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    searchScreen: @Composable () -> Unit,
    questionDetailScreen: @Composable (Long) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Search.route
    ) {
        composable(Screen.Search.route) {
            searchScreen()
        }
        
        composable(
            route = Screen.QuestionDetail.route,
            arguments = listOf(
                navArgument("questionId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val questionId = backStackEntry.arguments?.getLong("questionId") ?: return@composable
            questionDetailScreen(questionId)
        }
    }
} 