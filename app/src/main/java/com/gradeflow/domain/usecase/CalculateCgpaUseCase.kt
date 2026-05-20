package com.gradeflow.domain.usecase

import com.gradeflow.domain.model.CgpaResult
import com.gradeflow.domain.model.SemesterEntry
import com.gradeflow.domain.repository.ResultRepository
import com.gradeflow.domain.repository.UniversityRepository
import com.gradeflow.engine.FormulaEngine
import javax.inject.Inject

class CalculateCgpaUseCase @Inject constructor(
    private val formulaEngine: FormulaEngine,
    private val universityRepository: UniversityRepository,
    private val resultRepository: ResultRepository
) {
    suspend fun calculate(universityId: String, semesters: List<SemesterEntry>): Result<CgpaResult> {
        val config = universityRepository.getUniversityConfig(universityId)
            ?: return Result.failure(Exception("University not found"))
        val errors = formulaEngine.validateSemesters(semesters, config)
        if (errors.isNotEmpty()) return Result.failure(Exception(errors.first()))
        return formulaEngine.calculateCgpa(semesters, config).map { cgpa ->
            CgpaResult(universityId = universityId, universityName = config.universityName,
                cgpa = cgpa, totalSemesters = semesters.size, totalCredits = semesters.sumOf { it.credits },
                percentage = formulaEngine.cgpaToPercentage(cgpa, config), semesters = semesters)
        }
    }

    suspend fun saveResult(result: CgpaResult): Long = resultRepository.saveCgpaResult(result)
}
