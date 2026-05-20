package com.gradeflow.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gradeflow.presentation.screens.about.AboutScreen
import com.gradeflow.presentation.screens.cgpa.CgpaCalculatorScreen
import com.gradeflow.presentation.screens.home.HomeScreen
import com.gradeflow.presentation.screens.onboarding.OnboardingScreen
import com.gradeflow.presentation.screens.results.SavedResultsScreen
import com.gradeflow.presentation.screens.settings.SettingsScreen
import com.gradeflow.presentation.screens.tgpa.TgpaCalculatorScreen
import com.gradeflow.presentation.screens.university.UniversitySelectionScreen

@Composable
fun GradeFlowNavGraph(navController: NavHostController, startDestination: String = NavRoutes.HOME) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(tween(150)) },
        exitTransition = { fadeOut(tween(100)) },
        popEnterTransition = { fadeIn(tween(150)) },
        popExitTransition = { fadeOut(tween(100)) }
    ) {
        composable(NavRoutes.ONBOARDING) {
            OnboardingScreen(onFinish = { navController.navigate(NavRoutes.HOME) { popUpTo(NavRoutes.ONBOARDING) { inclusive = true } } })
        }
        composable(NavRoutes.HOME) {
            HomeScreen(
                onNavigateToUniversitySelection = { type -> navController.navigate(NavRoutes.universitySelection(type)) },
                onNavigateToResults = { navController.navigate(NavRoutes.SAVED_RESULTS) },
                onNavigateToSettings = { navController.navigate(NavRoutes.SETTINGS) },
                onNavigateToAbout = { navController.navigate(NavRoutes.ABOUT) }
            )
        }
        composable(NavRoutes.UNIVERSITY_SELECTION, arguments = listOf(navArgument("calculatorType") { type = NavType.StringType })) { entry ->
            val calcType = entry.arguments?.getString("calculatorType") ?: "tgpa"
            UniversitySelectionScreen(calculatorType = calcType,
                onUniversitySelected = { id -> navController.navigate(if (calcType == "tgpa") NavRoutes.tgpaCalculator(id) else NavRoutes.cgpaCalculator(id)) },
                onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.TGPA_CALCULATOR, arguments = listOf(navArgument("universityId") { type = NavType.StringType })) { entry ->
            TgpaCalculatorScreen(universityId = entry.arguments?.getString("universityId") ?: "",
                onBack = { navController.popBackStack() }, onNavigateToHome = { navController.navigate(NavRoutes.HOME) { popUpTo(NavRoutes.HOME) { inclusive = true } } })
        }
        composable(NavRoutes.CGPA_CALCULATOR, arguments = listOf(navArgument("universityId") { type = NavType.StringType })) { entry ->
            CgpaCalculatorScreen(universityId = entry.arguments?.getString("universityId") ?: "",
                onBack = { navController.popBackStack() }, onNavigateToHome = { navController.navigate(NavRoutes.HOME) { popUpTo(NavRoutes.HOME) { inclusive = true } } })
        }
        composable(NavRoutes.SAVED_RESULTS) { SavedResultsScreen(onBack = { navController.popBackStack() }) }
        composable(NavRoutes.SETTINGS) { SettingsScreen(onBack = { navController.popBackStack() }) }
        composable(NavRoutes.ABOUT) { AboutScreen(onBack = { navController.popBackStack() }) }
    }
}
