package com.gradeflow.engine

import com.gradeflow.data.model.BranchConfig
import com.gradeflow.data.model.SemesterPreset
import com.gradeflow.data.model.SubjectPreset
import com.gradeflow.data.model.UniversityConfig
import com.gradeflow.domain.model.SubjectEntry
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Branch Subject Engine - Manages branch-specific subject presets.
 *
 * Features:
 * - Auto-load subjects for a branch + semester combination
 * - Support honors/minors courses
 * - Filter audit courses
 * - Support elective placeholders
 *
 * Hierarchy: University → Branch → Semester → Subjects
 */
@Singleton
class BranchSubjectEngine @Inject constructor() {

    /**
     * Get available branches for a university.
     */
    fun getAvailableBranches(config: UniversityConfig): List<BranchConfig> {
        return config.branches.values.toList()
    }

    /**
     * Check if university has branch-specific subject presets.
     */
    fun hasBranchPresets(config: UniversityConfig): Boolean {
        return config.branches.isNotEmpty()
    }

    /**
     * Get branch config by ID.
     */
    fun getBranch(config: UniversityConfig, branchId: String): BranchConfig? {
        return config.branches[branchId]
    }

    /**
     * Get semester preset for a specific branch and semester.
     */
    fun getSemesterPreset(
        config: UniversityConfig,
        branchId: String,
        semesterNumber: Int
    ): SemesterPreset? {
        val branch = config.branches[branchId] ?: return null
        return branch.semesters["sem_$semesterNumber"]
            ?: branch.semesters[semesterNumber.toString()]
    }

    /**
     * Auto-load subjects as SubjectEntry list from preset.
     * Respects audit_courses_excluded setting.
     */
    fun loadPresetSubjects(
        config: UniversityConfig,
        branchId: String,
        semesterNumber: Int
    ): List<SubjectEntry> {
        val preset = getSemesterPreset(config, branchId, semesterNumber) ?: return emptyList()

        return preset.subjects
            .filter { subject ->
                // Exclude audit courses if config says so
                if (config.auditCoursesExcluded && subject.isAudit) false
                else true
            }
            .map { subject ->
                SubjectEntry(
                    name = subject.subjectName,
                    credits = subject.credits,
                    grade = "" // User fills in the grade
                )
            }
    }

    /**
     * Get total semesters for a branch.
     */
    fun getTotalSemesters(config: UniversityConfig, branchId: String): Int {
        return config.branches[branchId]?.totalSemesters ?: 8
    }

    /**
     * Check if honors is available for a branch.
     */
    fun isHonorsAvailable(config: UniversityConfig, branchId: String): Boolean {
        return config.branches[branchId]?.honorsAvailable ?: false
    }

    /**
     * Check if minors is available for a branch.
     */
    fun isMinorsAvailable(config: UniversityConfig, branchId: String): Boolean {
        return config.branches[branchId]?.minorsAvailable ?: false
    }

    /**
     * Get only elective subjects from a semester preset.
     */
    fun getElectives(
        config: UniversityConfig,
        branchId: String,
        semesterNumber: Int
    ): List<SubjectPreset> {
        val preset = getSemesterPreset(config, branchId, semesterNumber) ?: return emptyList()
        return preset.subjects.filter { it.isElective }
    }

    /**
     * Get total credits for a semester in a branch.
     */
    fun getSemesterCredits(
        config: UniversityConfig,
        branchId: String,
        semesterNumber: Int
    ): Int {
        val preset = getSemesterPreset(config, branchId, semesterNumber) ?: return 0
        return if (preset.totalCredits > 0) preset.totalCredits
        else preset.subjects.sumOf { it.credits }
    }
}
