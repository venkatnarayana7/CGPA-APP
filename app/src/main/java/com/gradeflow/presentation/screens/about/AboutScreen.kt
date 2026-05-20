package com.gradeflow.presentation.screens.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // App Logo
            Surface(
                Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("G", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(12.dp))

            // App Name
            Text("GradeFlow", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            // Subtitle
            Text(
                "Smart TGPA & CGPA Calculator",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "Supporting multiple Indian universities\nand grading systems.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Key Features Section
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Star, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Key Features", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.height(16.dp))

                    // Feature grid - 2 columns, 3 rows
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        FeatureChip(Icons.Outlined.Calculate, "TGPA Calculator")
                        FeatureChip(Icons.Outlined.Functions, "CGPA Calculator")
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        FeatureChip(Icons.Outlined.School, "Multi-University\nSupport")
                        FeatureChip(Icons.Outlined.AutoAwesome, "Auto Formula\nEngine")
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        FeatureChip(Icons.Outlined.CloudOff, "Offline Support")
                        FeatureChip(Icons.Outlined.Percent, "Percentage\nConversion")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Important Disclaimer
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0).copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(Modifier.padding(16.dp)) {
                    Column {
                        Text(
                            "Important",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFFF6B00),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Results are estimated based on available university grading systems. Always verify official academic results from your institution.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Developer Section
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Developed by", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                    Text("Venkata Narayana", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Passionate Android Developer", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                }
                Surface(
                    Modifier.size(40.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Code, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Social Links
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialButton(Icons.Filled.Code, "GitHub") {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/venkatnarayana7")))
                }
                Spacer(Modifier.width(32.dp))
                SocialButton(Icons.Filled.Work, "LinkedIn") {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://linkedin.com")))
                }
                Spacer(Modifier.width(32.dp))
                SocialButton(Icons.Filled.Email, "Email") {
                    context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:venkatnarayana@example.com")))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Bottom Links
            BottomLinkItem("Privacy Policy") {}
            BottomLinkItem("Terms & Conditions") {}
            BottomLinkItem("Rate Us") {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.gradeflow")))
                } catch (_: Exception) {}
            }
            BottomLinkItem("Send Feedback") {
                context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:venkatnarayana@example.com?subject=GradeFlow Feedback")))
            }

            Spacer(Modifier.height(20.dp))

            // Version
            Text(
                "Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(0.4f)
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FeatureChip(icon: ImageVector, label: String) {
    Column(
        Modifier.width(130.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            Modifier.size(36.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
        )
    }
}

@Composable
private fun SocialButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            Modifier.size(44.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(22.dp))
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(0.6f))
    }
}

@Composable
private fun BottomLinkItem(title: String, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    when (title) {
                        "Privacy Policy" -> Icons.Outlined.PrivacyTip
                        "Terms & Conditions" -> Icons.Outlined.Description
                        "Rate Us" -> Icons.Outlined.Star
                        else -> Icons.Outlined.Feedback
                    },
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onBackground.copy(0.3f), modifier = Modifier.size(20.dp))
        }
    }
}
