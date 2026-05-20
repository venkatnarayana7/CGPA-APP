package com.gradeflow.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    @SerialName("min_credits_per_subject") val minCreditsPerSubject: Int = 1
)
