package com.gradeflow.presentation.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gradeflow.presentation.components.GradeFlowButton
import kotlinx.coroutines.launch

data class OnboardingPage(val title: String, val description: String, val icon: ImageVector)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pages = listOf(
        OnboardingPage("Calculate TGPA", "Easily calculate your Term GPA with dynamic subject entry and real-time results.", Icons.Filled.Calculate),
        OnboardingPage("Multiple Universities", "Supports 10+ Indian universities with their specific grading systems.", Icons.Filled.School),
        OnboardingPage("Track Progress", "Save results, view history, and track academic progress over time.", Icons.Filled.TrendingUp)
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { context.getSharedPreferences("gradeflow_prefs", 0).edit().putBoolean("onboarding_completed", true).apply(); onFinish() }) {
                Text("Skip", color = MaterialTheme.colorScheme.primary)
            }
        }
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f).fillMaxWidth()) { page ->
            Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Surface(Modifier.size(120.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Box(contentAlignment = Alignment.Center) { Icon(pages[page].icon, null, Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary) }
                }
                Spacer(Modifier.height(40.dp))
                Text(pages[page].title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Text(pages[page].description, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground.copy(0.7f))
            }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.Center) {
            repeat(pages.size) { i -> Box(Modifier.padding(horizontal = 4.dp).size(if (pagerState.currentPage == i) 24.dp else 8.dp, 8.dp).clip(CircleShape).background(if (pagerState.currentPage == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(0.3f))) }
        }
        Spacer(Modifier.height(16.dp))
        if (pagerState.currentPage == pages.size - 1) GradeFlowButton("Get Started", onClick = { context.getSharedPreferences("gradeflow_prefs", 0).edit().putBoolean("onboarding_completed", true).apply(); onFinish() })
        else GradeFlowButton("Next", onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } })
    }
}
