package com.gradeflow.engine

import com.gradeflow.data.model.GradeEntry
import com.gradeflow.data.model.UniversityConfig
import com.gradeflow.domain.model.SemesterEntry
import com.gradeflow.domain.model.SubjectEntry
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.roundToInt

@Singleton
class FormulaEngine @Inject constructor() {

    fun calculateTgpa(subjects: List<SubjectEntry>, config: UniversityConfig): Result<Double> {
        if (subjects.isEmpty()) return Result.failure(Exception("At least one subject is required"))
        val totalCredits = subjects.sumOf { it.credits }
        if (totalCredits == 0) return Result.failure(Exception("Total credits cannot be zero"))
        val weightedSum = subjects.sumOf { subject ->
            val gradePoints = getGradePoints(subject.grade, config)
                ?: return Result.failure(Exception("Invalid grade: ${subject.grade}"))
            subject.credits * gradePoints
        }
        return Result.success(roundToDecimals(weightedSum / totalCredits, config.rounding))
    }

    fun calculateCgpa(semesters: List<SemesterEntry>, config: UniversityConfig): Result<Double> {
        if (semesters.isEmpty()) return Result.failure(Exception("At least one semester is required"))
        val totalCredits = semesters.sumOf { it.credits }
        if (totalCredits == 0) return Result.failure(Exception("Total credits cannot be zero"))
        val weightedSum = semesters.sumOf { it.gpa * it.credits }
        return Result.success(roundToDecimals(weightedSum / totalCredits, config.rounding))
    }

    fun cgpaToPercentage(cgpa: Double, config: UniversityConfig): Double {
        val percentage = when (config.percentageFormula) {
            "cgpa_multiplied" -> cgpa * config.percentageMultiplier
            "cgpa_subtracted_multiplied" -> (cgpa - config.percentageSubtractor) * config.percentageMultiplier
            else -> cgpa * 10.0
        }
        return roundToDecimals(percentage.coerceIn(0.0, 100.0), config.rounding)
    }

    fun getGradePoints(grade: String, config: UniversityConfig): Double? {
        return config.gradeMapping.find { it.grade.equals(grade, ignoreCase = true) }?.points
    }

    fun getAvailableGrades(config: UniversityConfig): List<GradeEntry> = config.gradeMapping

    fun validateSubjects(subjects: List<SubjectEntry>, config: UniversityConfig): List<String> {
        val errors = mutableListOf<String>()
        if (subjects.isEmpty()) { errors.add("At least one subject is required"); return errors }
        subjects.forEachIndexed { index, subject ->
            if (subject.credits < config.minCreditsPerSubject || subject.credits > config.maxCreditsPerSubject)
                errors.add("Subject ${index + 1}: Credits must be ${config.minCreditsPerSubject}-${config.maxCreditsPerSubject}")
            if (subject.grade.isBlank()) errors.add("Subject ${index + 1}: Grade is required")
            else if (getGradePoints(subject.grade, config) == null) errors.add("Subject ${index + 1}: Invalid grade")
        }
        return errors
    }

    fun validateSemesters(semesters: List<SemesterEntry>, config: UniversityConfig): List<String> {
        val errors = mutableListOf<String>()
        if (semesters.isEmpty()) { errors.add("At least one semester is required"); return errors }
        semesters.forEachIndexed { index, semester ->
            if (semester.gpa < 0 || semester.gpa > config.gradingScale) errors.add("Semester ${index + 1}: GPA must be 0-${config.gradingScale}")
            if (semester.credits <= 0) errors.add("Semester ${index + 1}: Credits must be > 0")
        }
        return errors
    }

    fun getClassification(cgpa: Double, gradingScale: Int = 10): String {
        val ratio = cgpa / gradingScale
        return when {
            ratio >= 0.9 -> "Outstanding"; ratio >= 0.8 -> "Excellent"
            ratio >= 0.7 -> "Very Good"; ratio >= 0.6 -> "Good"
            ratio >= 0.5 -> "Average"; else -> "Below Average"
        }
    }

    private fun roundToDecimals(value: Double, decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return (value * factor).roundToInt() / factor
    }
}
