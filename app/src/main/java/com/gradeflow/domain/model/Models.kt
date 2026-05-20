package com.gradeflow.domain.model

data class SubjectEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "", val credits: Int = 0,
    val grade: String = "", val gradePoints: Double = 0.0
)

data class SemesterEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val semesterNumber: Int = 1, val semesterName: String = "Semester 1",
    val gpa: Double = 0.0, val credits: Int = 0
)

data class TgpaResult(
    val id: Long = 0, val universityId: String = "", val universityName: String = "",
    val semesterName: String = "", val tgpa: Double = 0.0, val totalCredits: Int = 0,
    val totalSubjects: Int = 0, val percentage: Double = 0.0,
    val subjects: List<SubjectEntry> = emptyList(), val timestamp: Long = System.currentTimeMillis()
)

data class CgpaResult(
    val id: Long = 0, val universityId: String = "", val universityName: String = "",
    val cgpa: Double = 0.0, val totalSemesters: Int = 0, val totalCredits: Int = 0,
    val percentage: Double = 0.0, val semesters: List<SemesterEntry> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class University(
    val id: String, val name: String, val shortName: String,
    val location: String, val gradingScale: Int, val isFavorite: Boolean = false
)

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
}

sealed class CalculationError {
    data object NoSubjects : CalculationError()
    data object NoCredits : CalculationError()
    data object InvalidGrade : CalculationError()
    data object NoSemesters : CalculationError()
}
