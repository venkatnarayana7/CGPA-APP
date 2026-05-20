package com.gradeflow.engine

import com.gradeflow.data.model.BacklogPolicy
import com.gradeflow.data.model.UniversityConfig
import com.gradeflow.domain.model.SubjectEntry
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Backlog Handler - Manages repeat/backlog subject grade replacement policies.
 *
 * Supported policies:
 * - keep_highest: Always keep the best grade across attempts
 * - replace_grade: Replace old grade with new attempt grade
 * - average_attempts: Average grade points across all attempts
 * - latest_attempt: Always use the latest attempt
 *
 * Also handles:
 * - Max attempts tracking
 * - CGPA impact of backlogs
 * - Semester promotion rules with backlogs
 */
@Singleton
class BacklogHandler @Inject constructor() {

    /**
     * Result of backlog resolution for a subject.
     */
    data class BacklogResolution(
        val subjectName: String,
        val originalGrade: String,
        val originalPoints: Double,
        val newGrade: String,
        val newPoints: Double,
        val attemptCount: Int,
        val policyApplied: String
    )

    /**
     * Check if university has backlog policies configured.
     */
    fun hasBacklogPolicy(config: UniversityConfig): Boolean {
        return config.backlogPolicy != null
    }

    /**
     * Resolve grade for a subject that has been re-attempted.
     *
     * @param originalSubject The original attempt
     * @param newSubject The new attempt (re-exam/repeat)
     * @param config University configuration
     * @param attemptNumber Which attempt this is (2, 3, etc.)
     * @return BacklogResolution with the final grade to use
     */
    fun resolveBacklog(
        originalSubject: SubjectEntry,
        newSubject: SubjectEntry,
        config: UniversityConfig,
        attemptNumber: Int = 2
    ): BacklogResolution {
        val policy = config.backlogPolicy ?: return defaultResolution(originalSubject, newSubject, config)

        // Check max attempts
        if (policy.maxAttempts > 0 && attemptNumber > policy.maxAttempts) {
            return BacklogResolution(
                subjectName = originalSubject.name,
                originalGrade = originalSubject.grade,
                originalPoints = getPoints(originalSubject.grade, config),
                newGrade = originalSubject.grade,
                newPoints = getPoints(originalSubject.grade, config),
                attemptCount = attemptNumber,
                policyApplied = "max_attempts_exceeded"
            )
        }

        val originalPoints = getPoints(originalSubject.grade, config)
        val newPoints = getPoints(newSubject.grade, config)

        return when (policy.policyType) {
            "keep_highest" -> resolveKeepHighest(originalSubject, newSubject, originalPoints, newPoints, attemptNumber, config)
            "replace_grade" -> resolveReplaceGrade(originalSubject, newSubject, newPoints, attemptNumber)
            "average_attempts" -> resolveAverage(originalSubject, newSubject, originalPoints, newPoints, attemptNumber)
            "latest_attempt" -> resolveLatest(originalSubject, newSubject, newPoints, attemptNumber)
            else -> resolveKeepHighest(originalSubject, newSubject, originalPoints, newPoints, attemptNumber, config)
        }
    }

    /**
     * Process a list of subjects, applying backlog resolution where subjects
     * have been re-attempted. Groups by subject name and resolves each.
     *
     * @param allAttempts All attempts including original and repeats
     * @param config University configuration
     * @return List of resolved subjects (one per unique subject)
     */
    fun resolveAllBacklogs(
        allAttempts: List<SubjectEntry>,
        config: UniversityConfig
    ): List<SubjectEntry> {
        val policy = config.backlogPolicy ?: return allAttempts

        // Group by subject name
        val grouped = allAttempts.groupBy { it.name }

        return grouped.map { (_, attempts) ->
            if (attempts.size == 1) {
                attempts.first()
            } else {
                // Multiple attempts - resolve based on policy
                resolveMultipleAttempts(attempts, policy, config)
            }
        }
    }

    /**
     * Check if a student is eligible for semester promotion given their backlogs.
     *
     * @param backlogCount Number of current backlogs
     * @param currentCgpa Current CGPA
     * @param config University configuration
     * @return Pair of (eligible, reason)
     */
    fun checkPromotionEligibility(
        backlogCount: Int,
        currentCgpa: Double,
        config: UniversityConfig
    ): Pair<Boolean, String> {
        val policy = config.backlogPolicy ?: return Pair(true, "No backlog policy defined")
        val rules = policy.promotionRules ?: return Pair(true, "No promotion rules defined")

        if (backlogCount > rules.maxBacklogsForPromotion) {
            return Pair(false, "Too many backlogs (${backlogCount}/${rules.maxBacklogsForPromotion})")
        }

        if (rules.minCgpaForPromotion > 0 && currentCgpa < rules.minCgpaForPromotion) {
            return Pair(false, "CGPA below minimum (${currentCgpa}/${rules.minCgpaForPromotion})")
        }

        return Pair(true, "Eligible for promotion")
    }

    /**
     * Count backlogs (failed subjects) in a list.
     */
    fun countBacklogs(subjects: List<SubjectEntry>, config: UniversityConfig): Int {
        return subjects.count { subject ->
            val points = getPoints(subject.grade, config)
            points == 0.0 || config.failGrades.any { it.equals(subject.grade, ignoreCase = true) }
        }
    }

    // --- Private resolution methods ---

    private fun resolveKeepHighest(
        original: SubjectEntry, new: SubjectEntry,
        originalPoints: Double, newPoints: Double,
        attempt: Int, config: UniversityConfig
    ): BacklogResolution {
        return if (newPoints >= originalPoints) {
            BacklogResolution(original.name, original.grade, originalPoints, new.grade, newPoints, attempt, "keep_highest")
        } else {
            BacklogResolution(original.name, original.grade, originalPoints, original.grade, originalPoints, attempt, "keep_highest")
        }
    }

    private fun resolveReplaceGrade(
        original: SubjectEntry, new: SubjectEntry,
        newPoints: Double, attempt: Int
    ): BacklogResolution {
        return BacklogResolution(original.name, original.grade, 0.0, new.grade, newPoints, attempt, "replace_grade")
    }

    private fun resolveAverage(
        original: SubjectEntry, new: SubjectEntry,
        originalPoints: Double, newPoints: Double, attempt: Int
    ): BacklogResolution {
        val avg = (originalPoints + newPoints) / 2.0
        val finalGrade = if (newPoints >= originalPoints) new.grade else original.grade
        return BacklogResolution(original.name, original.grade, originalPoints, finalGrade, avg, attempt, "average_attempts")
    }

    private fun resolveLatest(
        original: SubjectEntry, new: SubjectEntry,
        newPoints: Double, attempt: Int
    ): BacklogResolution {
        return BacklogResolution(original.name, original.grade, 0.0, new.grade, newPoints, attempt, "latest_attempt")
    }

    private fun resolveMultipleAttempts(
        attempts: List<SubjectEntry>,
        policy: BacklogPolicy,
        config: UniversityConfig
    ): SubjectEntry {
        return when (policy.policyType) {
            "keep_highest" -> {
                attempts.maxByOrNull { getPoints(it.grade, config) } ?: attempts.last()
            }
            "replace_grade", "latest_attempt" -> {
                attempts.last()
            }
            "average_attempts" -> {
                val avgPoints = attempts.map { getPoints(it.grade, config) }.average()
                val best = attempts.maxByOrNull { getPoints(it.grade, config) } ?: attempts.last()
                best.copy(gradePoints = avgPoints)
            }
            else -> attempts.maxByOrNull { getPoints(it.grade, config) } ?: attempts.last()
        }
    }

    private fun defaultResolution(
        original: SubjectEntry, new: SubjectEntry, config: UniversityConfig
    ): BacklogResolution {
        val newPoints = getPoints(new.grade, config)
        return BacklogResolution(original.name, original.grade, getPoints(original.grade, config), new.grade, newPoints, 2, "default_replace")
    }

    private fun getPoints(grade: String, config: UniversityConfig): Double {
        return config.gradeMapping[grade]
            ?: config.gradeMapping.entries.find { it.key.equals(grade, ignoreCase = true) }?.value
            ?: 0.0
    }
}
