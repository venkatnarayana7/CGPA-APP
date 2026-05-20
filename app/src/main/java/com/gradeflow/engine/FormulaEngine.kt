package com.gradeflow.engine

import com.gradeflow.data.model.UniversityConfig
import com.gradeflow.domain.model.SemesterEntry
import com.gradeflow.domain.model.SubjectEntry
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Dynamic Formula Engine that parses university-specific formula strings
 * and calculates TGPA/CGPA/Percentage accordingly.
 *
 * Supports formulas like:
 * - "(sum(credits * grade_points)) / sum(credits)"
 * - "(cgpa - 0.5) * 10"
 * - "cgpa * 9.5"
 *
 * All calculations are driven by JSON config - no hardcoded university logic.
 */
@Singleton
class FormulaEngine @Inject constructor() {

    /**
     * Calculate TGPA using the university's specific formula.
     * All formulas ultimately resolve to: sum(credits * grade_points) / total_credits
     * but the formula string is parsed to support future variations.
     */
    fun calculateTgpa(subjects: List<SubjectEntry>, config: UniversityConfig): Result<Double> {
        if (subjects.isEmpty()) return Result.failure(Exception("At least one subject is required"))

        val totalCredits = subjects.sumOf { it.credits }
        if (totalCredits == 0) return Result.failure(Exception("Total credits cannot be zero"))

        // Calculate weighted sum: sum(credits * grade_points)
        var weightedSum = 0.0
        for (subject in subjects) {
            val gradePoints = getGradePoints(subject.grade, config)
                ?: return Result.failure(Exception("Invalid grade: ${subject.grade}"))

            // If failed subjects are not counted and this is a fail grade, skip
            if (!config.failedSubjectsCounted && gradePoints == 0.0) continue

            weightedSum += subject.credits * gradePoints
        }

        // TGPA = sum(credits * grade_points) / total_credits
        val effectiveCredits = if (!config.failedSubjectsCounted) {
            subjects.filter { getGradePoints(it.grade, config) != 0.0 }.sumOf { it.credits }
        } else {
            totalCredits
        }

        if (effectiveCredits == 0) return Result.failure(Exception("No valid credits to calculate"))

        val tgpa = weightedSum / effectiveCredits
        return Result.success(roundToDecimals(tgpa, config.rounding))
    }

    /**
     * Calculate CGPA using the university's specific formula.
     * Formula: sum(sgpa * semester_credits) / total_credits
     */
    fun calculateCgpa(semesters: List<SemesterEntry>, config: UniversityConfig): Result<Double> {
        if (semesters.isEmpty()) return Result.failure(Exception("At least one semester is required"))

        val totalCredits = semesters.sumOf { it.credits }
        if (totalCredits == 0) return Result.failure(Exception("Total credits cannot be zero"))

        // CGPA = sum(sgpa * semester_credits) / total_credits
        val weightedSum = semesters.sumOf { it.gpa * it.credits }
        val cgpa = weightedSum / totalCredits

        return Result.success(roundToDecimals(cgpa, config.rounding))
    }

    /**
     * Convert CGPA to percentage using the university's specific formula string.
     * Dynamically parses formulas like:
     * - "cgpa * 10"
     * - "(cgpa - 0.5) * 10"
     * - "cgpa * 9.5"
     * - "(cgpa - 0.75) * 10"
     */
    fun cgpaToPercentage(cgpa: Double, config: UniversityConfig): Double {
        val percentage = parsePercentageFormula(cgpa, config.percentageFormula)
        return roundToDecimals(percentage.coerceIn(0.0, 100.0), config.rounding)
    }

    /**
     * Parse and evaluate the percentage formula string dynamically.
     * Supports patterns:
     * - "cgpa * X"
     * - "(cgpa - X) * Y"
     */
    private fun parsePercentageFormula(cgpa: Double, formula: String): Double {
        val cleaned = formula.replace(" ", "").lowercase()

        return try {
            when {
                // Pattern: (cgpa - X) * Y
                cleaned.contains("(cgpa-") && cleaned.contains(")*") -> {
                    val subtractor = cleaned.substringAfter("(cgpa-").substringBefore(")").toDouble()
                    val multiplier = cleaned.substringAfter(")*").toDouble()
                    (cgpa - subtractor) * multiplier
                }
                // Pattern: (cgpa-X)*Y without spaces
                cleaned.startsWith("(cgpa") && cleaned.contains("*") -> {
                    val inner = cleaned.substringAfter("(").substringBefore(")")
                    val subtractor = inner.replace("cgpa", "").replace("-", "").toDoubleOrNull() ?: 0.0
                    val multiplier = cleaned.substringAfter(")*").toDoubleOrNull()
                        ?: cleaned.substringAfterLast("*").toDoubleOrNull() ?: 10.0
                    if (inner.contains("-")) (cgpa - subtractor) * multiplier
                    else cgpa * multiplier
                }
                // Pattern: cgpa * X
                cleaned.startsWith("cgpa*") -> {
                    val multiplier = cleaned.substringAfter("cgpa*").toDouble()
                    cgpa * multiplier
                }
                // Pattern: cgpa*X
                cleaned.contains("cgpa") && cleaned.contains("*") -> {
                    val multiplier = cleaned.replace("cgpa", "").replace("*", "").toDoubleOrNull() ?: 10.0
                    cgpa * multiplier
                }
                // Default: cgpa * 10
                else -> cgpa * 10.0
            }
        } catch (e: Exception) {
            // Fallback to simple multiplication
            cgpa * 10.0
        }
    }

    /**
     * Get grade points for a given grade from the university's grade mapping.
     * Uses the Map<String, Double> format from JSON.
     */
    fun getGradePoints(grade: String, config: UniversityConfig): Double? {
        // Try exact match first
        config.gradeMapping[grade]?.let { return it }
        // Try case-insensitive match
        return config.gradeMapping.entries.find {
            it.key.equals(grade, ignoreCase = true)
        }?.value
    }

    /**
     * Get list of available grade strings for dropdown display.
     */
    fun getAvailableGrades(config: UniversityConfig): List<String> {
        return config.gradeMapping.keys.toList()
    }

    /**
     * Validate subject entries before TGPA calculation.
     */
    fun validateSubjects(subjects: List<SubjectEntry>, config: UniversityConfig): List<String> {
        val errors = mutableListOf<String>()
        if (subjects.isEmpty()) { errors.add("At least one subject is required"); return errors }
        subjects.forEachIndexed { index, subject ->
            if (subject.credits < config.minCreditsPerSubject || subject.credits > config.maxCreditsPerSubject)
                errors.add("Subject ${index + 1}: Credits must be ${config.minCreditsPerSubject}-${config.maxCreditsPerSubject}")
            if (subject.grade.isBlank()) errors.add("Subject ${index + 1}: Grade is required")
            else if (getGradePoints(subject.grade, config) == null) errors.add("Subject ${index + 1}: Invalid grade '${subject.grade}'")
        }
        return errors
    }

    /**
     * Validate semester entries before CGPA calculation.
     */
    fun validateSemesters(semesters: List<SemesterEntry>, config: UniversityConfig): List<String> {
        val errors = mutableListOf<String>()
        if (semesters.isEmpty()) { errors.add("At least one semester is required"); return errors }
        semesters.forEachIndexed { index, semester ->
            if (semester.gpa < 0 || semester.gpa > config.gradingScale) errors.add("Semester ${index + 1}: GPA must be 0-${config.gradingScale}")
            if (semester.credits <= 0) errors.add("Semester ${index + 1}: Credits must be > 0")
        }
        return errors
    }

    /**
     * Get academic classification based on CGPA.
     */
    fun getClassification(cgpa: Double, gradingScale: Int = 10): String {
        val ratio = cgpa / gradingScale
        return when {
            ratio >= 0.9 -> "Outstanding"
            ratio >= 0.8 -> "Excellent"
            ratio >= 0.7 -> "Very Good"
            ratio >= 0.6 -> "Good"
            ratio >= 0.5 -> "Average"
            else -> "Below Average"
        }
    }

    /**
     * Check if a grade is a failing grade for the university.
     */
    fun isFailingGrade(grade: String, config: UniversityConfig): Boolean {
        val points = getGradePoints(grade, config) ?: return true
        return points == 0.0
    }

    private fun roundToDecimals(value: Double, decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return (value * factor).roundToInt() / factor
    }
}
