package com.gradeflow.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gradeflow.presentation.theme.*

@Composable
fun GradeFlowButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, isLoading: Boolean = false) {
    Button(onClick = onClick, modifier = modifier.fillMaxWidth().height(56.dp), enabled = enabled && !isLoading,
        shape = ButtonShape, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)) {
        if (isLoading) CircularProgressIndicator(Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
        else Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

@Composable
fun GradeFlowOutlinedButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    OutlinedButton(onClick = onClick, modifier = modifier.fillMaxWidth().height(52.dp), shape = ButtonShape) {
        icon?.let { Icon(it, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)) }
        Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun GradeFlowCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = CardShape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun GpaResultCard(title: String, gpaValue: String, percentage: String, subtitle: String = "", modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = FloatingCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary.copy(0.8f))
            Spacer(Modifier.height(8.dp))
            Text(gpaValue, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            if (subtitle.isNotBlank()) { Spacer(Modifier.height(4.dp)); Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(0.7f)) }
            Spacer(Modifier.height(12.dp))
            Surface(shape = ChipShape, color = MaterialTheme.colorScheme.onPrimary.copy(0.2f)) {
                Text("Percentage: $percentage%", Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun UniversityCard(name: String, shortName: String, location: String, isFavorite: Boolean, onClick: () -> Unit, onFavoriteClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().clickable { onClick() }, shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(44.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Box(contentAlignment = Alignment.Center) { Text(shortName.take(2), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)) {
                        Text(shortName, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(location, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(0.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onFavoriteClick, modifier = Modifier.size(36.dp)) {
                Icon(if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.3f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun SubjectInputCard(index: Int, subjectName: String, credits: String, selectedGrade: String, availableGrades: List<String>,
    onNameChange: (String) -> Unit, onCreditsChange: (String) -> Unit, onGradeChange: (String) -> Unit, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Card(modifier = modifier.fillMaxWidth(), shape = CardShape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Subject ${index + 1}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.Close, "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = subjectName, onValueChange = onNameChange, label = { Text("Subject Name") }, modifier = Modifier.fillMaxWidth(), shape = InputFieldShape, singleLine = true)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = credits, onValueChange = onCreditsChange, label = { Text("Credits") }, modifier = Modifier.weight(1f), shape = InputFieldShape, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Box(Modifier.weight(1f)) {
                    OutlinedTextField(value = selectedGrade, onValueChange = {}, label = { Text("Grade") }, modifier = Modifier.fillMaxWidth(), shape = InputFieldShape, singleLine = true, readOnly = true, enabled = false,
                        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) }, colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(0.6f)))
                    Box(Modifier.matchParentSize().clickable { expanded = true })
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        availableGrades.forEach { grade -> DropdownMenuItem(text = { Text(grade) }, onClick = { onGradeChange(grade); expanded = false }) }
                    }
                }
            }
        }
    }
}

@Composable
fun SemesterInputCard(index: Int, gpa: String, credits: String, gradingScale: Int, onGpaChange: (String) -> Unit, onCreditsChange: (String) -> Unit, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = CardShape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Semester ${index + 1}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.Close, "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = gpa, onValueChange = onGpaChange, label = { Text("GPA (0-$gradingScale)") }, modifier = Modifier.weight(1f), shape = InputFieldShape, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = credits, onValueChange = onCreditsChange, label = { Text("Credits") }, modifier = Modifier.weight(1f), shape = InputFieldShape, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
    }
}

@Composable
fun GradeFlowSearchBar(query: String, onQueryChange: (String) -> Unit, placeholder: String = "Search...", modifier: Modifier = Modifier) {
    OutlinedTextField(value = query, onValueChange = onQueryChange, modifier = modifier.fillMaxWidth(), placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Filled.Search, "Search", tint = MaterialTheme.colorScheme.onSurface.copy(0.5f)) },
        trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { onQueryChange("") }) { Icon(Icons.Filled.Clear, "Clear") } },
        shape = SearchBarShape, singleLine = true)
}

@Composable
fun EmptyStateView(icon: ImageVector = Icons.Outlined.Info, title: String, subtitle: String = "", modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(0.4f))
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground.copy(0.7f))
        if (subtitle.isNotBlank()) { Spacer(Modifier.height(8.dp)); Text(subtitle, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground.copy(0.5f)) }
    }
}

@Composable
fun LoadingView(modifier: Modifier = Modifier, message: String = "Loading...") {
    Column(modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 3.dp)
        Spacer(Modifier.height(16.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(0.6f))
    }
}

@Composable
fun FeatureCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().clickable { onClick() }, shape = CardShape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(52.dp), shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp)) }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(2.dp)); Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f)) }
            Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurface.copy(0.3f))
        }
    }
}
