package com.gradeflow.engine

import com.gradeflow.data.model.GraceRules
import com.gradeflow.data.model.UniversityConfig
import com.gradeflow.domain.model.SubjectEntry
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Grace Mark Calculator - Handles university-specific grace mark policies.
 *
 * Features:
 * - Grace pass: student barely fails but gets promoted with grace marks
 * - Conditional pass: pass with conditions (min marks threshold)
 * - Max grace subjects limit per semester
 * - Grace applicable only to specific grades
 *
 * Example scenarios:
 * - Student scored 35/100, pass is 40. If grace = 5 marks, student passes.
 * - VTU: Maximum 5 grace marks across max 2 subjects per semester.
 * - Anna University: Grace marks only for final-year students.
 */
@Singleton
class GraceMarkCalculator @Inject constructor() {

    /**
     * Result of grace mark evaluation for a subject.
     */
    data class GraceResult(
        val originalGrade: String,
        val originalPoints: Double,
        val graceApplied: Boolean,
        val newGrade: String,
        val newPoints: Double,
        val graceMarksUsed: Int
    )

    /**
     * Check if grace marks are available for this university.
     */
    fun isGraceAvailable(config: UniversityConfig): Boolean {
        return config.graceRules?.graceEnabled == true
    }

    /**
     * Evaluate if a subject can receive grace marks.
     *
     * @param subject The subject to evaluate
     * @param config University configuration
     * @param alreadyGracedCount How many subjects already received grace in this semester
     * @return GraceResult indicating if grace was applied
     */
    fun evaluateGrace(
        subject: SubjectEntry,
        config: UniversityConfig,
        alreadyGracedCount: Int = 0
    ): GraceResult {
        val graceRules = config.graceRules ?: return noGrace(subject, config)

        if (!graceRules.graceEnabled) return noGrace(subject, config)

        val currentPoints = getPoints(subject.grade, config)

        // Check if this grade is eligible for grace
        if (graceRules.graceApplicableGrades.isNotEmpty() &&
            subject.grade !in graceRules.graceApplicableGrades
        ) {
            return noGrace(subject, config)
        }

        // Check if max grace subjects limit reached
        if (graceRules.maxSubjectsForGrace > 0 && alreadyGracedCount >= graceRules.maxSubjectsForGrace) {
            return noGrace(subject, config)
        }

        // Only apply grace to failing subjects
        if (currentPoints > 0.0) return noGrace(subject, config)

        // Grace applied: promote to lowest passing grade
        val lowestPassGrade = findLowestPassGrade(config)
        val lowestPassPoints = lowestPassGrade?.let { config.gradeMapping[it] } ?: 0.0

        return GraceResult(
            originalGrade = subject.grade,
            originalPoints = currentPoints,
            graceApplied = true,
            newGrade = lowestPassGrade ?: subject.grade,
            newPoints = lowestPassPoints,
            graceMarksUsed = graceRules.maxGraceMarks
        )
    }

    /**
     * Apply grace marks to a list of subjects.
     * Respects the max subjects limit.
     *
     * @return Pair of (modified subjects, total grace marks used)
     */
    fun applyGraceToSubjects(
        subjects: List<SubjectEntry>,
        config: UniversityConfig
    ): Pair<List<SubjectEntry>, Int> {
        val graceRules = config.graceRules
            ?: return Pair(subjects, 0)

        if (!graceRules.graceEnabled) return Pair(subjects, 0)

        var gracedCount = 0
        var totalGraceUsed = 0
        val maxGraceSubjects = if (graceRules.maxSubjectsForGrace > 0)
            graceRules.maxSubjectsForGrace else Int.MAX_VALUE

        val modifiedSubjects = subjects.map { subject ->
            if (gracedCount >= maxGraceSubjects) return@map subject

            val result = evaluateGrace(subject, config, gracedCount)
            if (result.graceApplied) {
                gracedCount++
                totalGraceUsed += result.graceMarksUsed
                subject.copy(grade = result.newGrade, gradePoints = result.newPoints)
            } else {
                subject
            }
        }

        return Pair(modifiedSubjects, totalGraceUsed)
    }

    /**
     * Check conditional pass eligibility.
     * Conditional pass: student didn't fully pass but meets minimum threshold.
     */
    fun isConditionalPass(
        subject: SubjectEntry,
        config: UniversityConfig,
        actualMarks: Int = 0
    ): Boolean {
        val graceRules = config.graceRules ?: return false
        if (!graceRules.conditionalPassEnabled) return false

        val points = getPoints(subject.grade, config)
        if (points > 0.0) return false // Already passing

        // Check if within conditional pass range
        val passMarks = graceRules.conditionalPassMinMarks
        val maxDeficit = graceRules.conditionalPassMaxDeficit

        return actualMarks >= (passMarks - maxDeficit) && actualMarks < passMarks
    }

    private fun noGrace(subject: SubjectEntry, config: UniversityConfig): GraceResult {
        val points = getPoints(subject.grade, config)
        return GraceResult(subject.grade, points, false, subject.grade, points, 0)
    }

    private fun getPoints(grade: String, config: UniversityConfig): Double {
        return config.gradeMapping[grade]
            ?: config.gradeMapping.entries.find { it.key.equals(grade, ignoreCase = true) }?.value
            ?: 0.0
    }

    /**
     * Find the lowest passing grade in the grading system.
     */
    private fun findLowestPassGrade(config: UniversityConfig): String? {
        return config.gradeMapping.entries
            .filter { it.value > 0.0 }
            .minByOrNull { it.value }
            ?.key
    }
}
