package com.gradeflow.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Root university configuration loaded from JSON.
 * Supports: University → Regulation → College → Branch → Semester → Subjects
 */
@Serializable
data class UniversityConfig(
    @SerialName("university_id") val universityId: String,
    @SerialName("university_name") val universityName: String,
    @SerialName("short_name") val shortName: String,
    @SerialName("location") val location: String = "",
    @SerialName("grading_scale") val gradingScale: Int = 10,
    @SerialName("tgpa_formula") val tgpaFormula: String = "(sum(credits * grade_points)) / sum(credits)",
    @SerialName("cgpa_formula") val cgpaFormula: String = "(sum(sgpa * semester_credits)) / total_credits",
    @SerialName("percentage_formula") val percentageFormula: String = "cgpa * 10",
    @SerialName("rounding") val rounding: Int = 2,
    @SerialName("failed_subjects_counted") val failedSubjectsCounted: Boolean = true,
    @SerialName("audit_courses_excluded") val auditCoursesExcluded: Boolean = false,
    @SerialName("repeat_subject_replacement") val repeatSubjectReplacement: Boolean = false,
    @SerialName("arrear_subjects_counted") val arrearSubjectsCounted: Boolean = false,
    @SerialName("grade_mapping") val gradeMapping: Map<String, Double> = emptyMap(),
    @SerialName("fail_grades") val failGrades: List<String> = emptyList(),
    @SerialName("regulations") val regulations: List<String> = emptyList(),
    @SerialName("max_credits_per_subject") val maxCreditsPerSubject: Int = 8,
    @SerialName("min_credits_per_subject") val minCreditsPerSubject: Int = 1,

    // Advanced: Regulation-specific overrides
    @SerialName("regulation_configs") val regulationConfigs: Map<String, RegulationConfig> = emptyMap(),

    // Advanced: Autonomous college overrides
    @SerialName("autonomous_colleges") val autonomousColleges: List<AutonomousCollegeConfig> = emptyList(),

    // Advanced: Branch-specific subject presets
    @SerialName("branches") val branches: Map<String, BranchConfig> = emptyMap(),

    // Advanced: Grace mark rules
    @SerialName("grace_rules") val graceRules: GraceRules? = null,

    // Advanced: Backlog policies
    @SerialName("backlog_policy") val backlogPolicy: BacklogPolicy? = null
)

/**
 * Regulation-specific configuration that overrides parent university settings.
 * Example: R18 and R22 can have different grade mappings and formulas.
 */
@Serializable
data class RegulationConfig(
    @SerialName("regulation_id") val regulationId: String = "",
    @SerialName("regulation_name") val regulationName: String = "",
    @SerialName("effective_from") val effectiveFrom: Int = 0,
    @SerialName("grade_mapping") val gradeMapping: Map<String, Double>? = null,
    @SerialName("percentage_formula") val percentageFormula: String? = null,
    @SerialName("tgpa_formula") val tgpaFormula: String? = null,
    @SerialName("cgpa_formula") val cgpaFormula: String? = null,
    @SerialName("pass_criteria") val passCriteria: Double? = null,
    @SerialName("fail_grades") val failGrades: List<String>? = null,
    @SerialName("grace_rules") val graceRules: GraceRules? = null,
    @SerialName("backlog_policy") val backlogPolicy: BacklogPolicy? = null
)

/**
 * Autonomous college that inherits from parent university
 * but can override specific rules.
 */
@Serializable
data class AutonomousCollegeConfig(
    @SerialName("college_id") val collegeId: String,
    @SerialName("college_name") val collegeName: String,
    @SerialName("location") val location: String = "",
    @SerialName("parent_university") val parentUniversity: String = "",
    @SerialName("grade_mapping_override") val gradeMappingOverride: Map<String, Double>? = null,
    @SerialName("percentage_formula_override") val percentageFormulaOverride: String? = null,
    @SerialName("additional_grades") val additionalGrades: Map<String, Double>? = null,
    @SerialName("custom_branches") val customBranches: Map<String, BranchConfig>? = null
)

/**
 * Branch-specific configuration with semester subject presets.
 */
@Serializable
data class BranchConfig(
    @SerialName("branch_id") val branchId: String = "",
    @SerialName("branch_name") val branchName: String = "",
    @SerialName("total_semesters") val totalSemesters: Int = 8,
    @SerialName("honors_available") val honorsAvailable: Boolean = false,
    @SerialName("minors_available") val minorsAvailable: Boolean = false,
    @SerialName("semesters") val semesters: Map<String, SemesterPreset> = emptyMap()
)

/**
 * Preset subjects for a specific semester in a branch.
 */
@Serializable
data class SemesterPreset(
    @SerialName("semester_number") val semesterNumber: Int = 1,
    @SerialName("total_credits") val totalCredits: Int = 0,
    @SerialName("subjects") val subjects: List<SubjectPreset> = emptyList()
)

/**
 * Predefined subject with name and credits.
 */
@Serializable
data class SubjectPreset(
    @SerialName("subject_name") val subjectName: String,
    @SerialName("subject_code") val subjectCode: String = "",
    @SerialName("credits") val credits: Int,
    @SerialName("is_lab") val isLab: Boolean = false,
    @SerialName("is_elective") val isElective: Boolean = false,
    @SerialName("is_audit") val isAudit: Boolean = false
)

/**
 * Grace mark rules for conditional pass.
 */
@Serializable
data class GraceRules(
    @SerialName("grace_enabled") val graceEnabled: Boolean = false,
    @SerialName("max_grace_marks") val maxGraceMarks: Int = 0,
    @SerialName("max_subjects_for_grace") val maxSubjectsForGrace: Int = 0,
    @SerialName("grace_applicable_grades") val graceApplicableGrades: List<String> = emptyList(),
    @SerialName("conditional_pass_enabled") val conditionalPassEnabled: Boolean = false,
    @SerialName("conditional_pass_min_marks") val conditionalPassMinMarks: Int = 0,
    @SerialName("conditional_pass_max_deficit") val conditionalPassMaxDeficit: Int = 5
)

/**
 * Backlog/repeat subject handling policies.
 */
@Serializable
data class BacklogPolicy(
    @SerialName("policy_type") val policyType: String = "keep_highest",
    @SerialName("max_attempts") val maxAttempts: Int = 0,
    @SerialName("replace_grade") val replaceGrade: Boolean = true,
    @SerialName("keep_highest") val keepHighest: Boolean = true,
    @SerialName("average_attempts") val averageAttempts: Boolean = false,
    @SerialName("backlog_cgpa_impact") val backlogCgpaImpact: Boolean = true,
    @SerialName("max_backlogs_per_semester") val maxBacklogsPerSemester: Int = 0,
    @SerialName("promotion_rules") val promotionRules: PromotionRules? = null
)

/**
 * Rules for semester promotion with backlogs.
 */
@Serializable
data class PromotionRules(
    @SerialName("max_backlogs_for_promotion") val maxBacklogsForPromotion: Int = 4,
    @SerialName("min_cgpa_for_promotion") val minCgpaForPromotion: Double = 0.0,
    @SerialName("mandatory_subjects_pass") val mandatorySubjectsPass: Boolean = false
)
