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
    @SerialName("tgpa_formula") val tgpaFormula: String = "weighted_average",
    @SerialName("cgpa_formula") val cgpaFormula: String = "weighted_average",
    @SerialName("percentage_formula") val percentageFormula: String = "",
    @SerialName("percentage_multiplier") val percentageMultiplier: Double = 0.0,
    @SerialName("percentage_subtractor") val percentageSubtractor: Double = 0.0,
    @SerialName("rounding") val rounding: Int = 2,
    @SerialName("grade_mapping") val gradeMapping: List<GradeEntry> = emptyList(),
    @SerialName("pass_grade_point") val passGradePoint: Double = 4.0,
    @SerialName("fail_grades") val failGrades: List<String> = emptyList(),
    @SerialName("special_rules") val specialRules: List<SpecialRule> = emptyList(),
    @SerialName("regulations") val regulations: List<String> = emptyList(),
    @SerialName("max_credits_per_subject") val maxCreditsPerSubject: Int = 6,
    @SerialName("min_credits_per_subject") val minCreditsPerSubject: Int = 1
)

@Serializable
data class GradeEntry(
    @SerialName("grade") val grade: String,
    @SerialName("points") val points: Double,
    @SerialName("description") val description: String = "",
    @SerialName("range_min") val rangeMin: Int = 0,
    @SerialName("range_max") val rangeMax: Int = 100
)

@Serializable
data class SpecialRule(
    @SerialName("rule_type") val ruleType: String,
    @SerialName("description") val description: String,
    @SerialName("value") val value: String = ""
)
