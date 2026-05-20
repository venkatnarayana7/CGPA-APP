package com.gradeflow.presentation.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onNavigateToOnboarding: () -> Unit, onNavigateToHome: () -> Unit) {
    val context = LocalContext.current

    val animScale = remember { Animatable(0.8f) }
    val animAlpha = remember { Animatable(0f) }
    val animOffsetY = remember { Animatable(40f) }

    LaunchedEffect(Unit) {
        launch {
            animScale.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        }
        launch {
            animAlpha.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        }
        launch {
            animOffsetY.animateTo(0f, tween(900, easing = FastOutSlowInEasing))
        }
        delay(2200)
        val prefs = context.getSharedPreferences("gradeflow_prefs", 0)
        if (prefs.getBoolean("onboarding_completed", false)) onNavigateToHome() else onNavigateToOnboarding()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                scaleX = animScale.value
                scaleY = animScale.value
                alpha = animAlpha.value
                translationY = animOffsetY.value
            }
        ) {
            Surface(
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "G",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "GradeFlow",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Your GPA Companion",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}
