package com.gradeflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.gradeflow.presentation.navigation.GradeFlowNavGraph
import com.gradeflow.presentation.navigation.NavRoutes
import com.gradeflow.presentation.theme.GradeFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Determine start destination immediately - no second splash needed
        val prefs = getSharedPreferences("gradeflow_prefs", 0)
        val onboardingDone = prefs.getBoolean("onboarding_completed", false)
        val startRoute = if (onboardingDone) NavRoutes.HOME else NavRoutes.ONBOARDING

        setContent {
            GradeFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    GradeFlowNavGraph(
                        navController = navController,
                        startDestination = startRoute
                    )
                }
            }
        }
    }
}
