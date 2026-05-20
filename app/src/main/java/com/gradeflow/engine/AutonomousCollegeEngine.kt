package com.gradeflow.engine

import com.gradeflow.data.model.AutonomousCollegeConfig
import com.gradeflow.data.model.UniversityConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Autonomous College Engine - Handles colleges under the same university
 * that have custom grading systems while inheriting from the parent.
 *
 * Inheritance model:
 * 1. Start with parent university config
 * 2. Apply college-specific overrides (grade mapping, formulas)
 * 3. Merge additional grades if provided
 *
 * Example: An autonomous college under Anna University might use
 * a slightly different percentage formula but same grade scale.
 */
@Singleton
class AutonomousCollegeEngine @Inject constructor() {

    /**
     * Resolve config for a specific autonomous college.
     * Inherits from parent university and applies college overrides.
     *
     * @param baseConfig The parent university configuration
     * @param collegeId The autonomous college ID
     * @return Resolved config with college overrides applied
     */
    fun resolveForCollege(
        baseConfig: UniversityConfig,
        collegeId: String?
    ): UniversityConfig {
        if (collegeId.isNullOrBlank()) return baseConfig

        val college = baseConfig.autonomousColleges.find { it.collegeId == collegeId }
            ?: return baseConfig

        return applyCollegeOverrides(baseConfig, college)
    }

    /**
     * Get list of autonomous colleges for a university.
     */
    fun getAutonomousColleges(config: UniversityConfig): List<AutonomousCollegeConfig> {
        return config.autonomousColleges
    }

    /**
     * Check if university has autonomous colleges.
     */
    fun hasAutonomousColleges(config: UniversityConfig): Boolean {
        return config.autonomousColleges.isNotEmpty()
    }

    /**
     * Find a college by name (partial match).
     */
    fun searchColleges(config: UniversityConfig, query: String): List<AutonomousCollegeConfig> {
        if (query.isBlank()) return config.autonomousColleges
        return config.autonomousColleges.filter {
            it.collegeName.contains(query, ignoreCase = true) ||
                    it.collegeId.contains(query, ignoreCase = true)
        }
    }

    /**
     * Apply college-specific overrides to base university config.
     * Uses inheritance: only override what's explicitly specified.
     */
    private fun applyCollegeOverrides(
        base: UniversityConfig,
        college: AutonomousCollegeConfig
    ): UniversityConfig {
        // Build merged grade mapping
        val mergedGradeMapping = buildMergedGradeMapping(base, college)

        // Apply percentage formula override if specified
        val percentageFormula = college.percentageFormulaOverride ?: base.percentageFormula

        return base.copy(
            universityName = "${base.universityName} - ${college.collegeName}",
            shortName = "${base.shortName} (${college.collegeId})",
            gradeMapping = mergedGradeMapping,
            percentageFormula = percentageFormula
        )
    }

    /**
     * Build merged grade mapping:
     * 1. Start with parent grade mapping
     * 2. If college has complete override, use that
     * 3. If college has additional grades, merge them in
     */
    private fun buildMergedGradeMapping(
        base: UniversityConfig,
        college: AutonomousCollegeConfig
    ): Map<String, Double> {
        // If college has a complete override, use it entirely
        college.gradeMappingOverride?.let { return it }

        // Otherwise, merge additional grades into base
        val merged = base.gradeMapping.toMutableMap()
        college.additionalGrades?.let { merged.putAll(it) }
        return merged
    }
}
