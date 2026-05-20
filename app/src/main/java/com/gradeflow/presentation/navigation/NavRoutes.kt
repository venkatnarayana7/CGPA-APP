package com.gradeflow.presentation.navigation

object NavRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val UNIVERSITY_SELECTION = "university_selection/{calculatorType}"
    const val TGPA_CALCULATOR = "tgpa_calculator/{universityId}"
    const val CGPA_CALCULATOR = "cgpa_calculator/{universityId}"
    const val SAVED_RESULTS = "saved_results"
    const val SETTINGS = "settings"
    const val ABOUT = "about"

    fun universitySelection(calculatorType: String) = "university_selection/$calculatorType"
    fun tgpaCalculator(universityId: String) = "tgpa_calculator/$universityId"
    fun cgpaCalculator(universityId: String) = "cgpa_calculator/$universityId"
}
