package com.gradeflow.domain.usecase

import com.gradeflow.domain.model.SubjectEntry
import com.gradeflow.domain.model.TgpaResult
import com.gradeflow.domain.repository.ResultRepository
import com.gradeflow.domain.repository.UniversityRepository
import com.gradeflow.engine.FormulaEngine
import javax.inject.Inject

class CalculateTgpaUseCase @Inject constructor(
    private val formulaEngine: FormulaEngine,
    private val universityRepository: UniversityRepository,
    private val resultRepository: ResultRepository
) {
    suspend fun calculate(universityId: String, semesterName: String, subjects: List<SubjectEntry>): Result<TgpaResult> {
        val config = universityRepository.getUniversityConfig(universityId)
            ?: return Result.failure(Exception("University not found"))
        val errors = formulaEngine.validateSubjects(subjects, config)
        if (errors.isNotEmpty()) return Result.failure(Exception(errors.first()))
        val mappedSubjects = subjects.map { it.copy(gradePoints = formulaEngine.getGradePoints(it.grade, config) ?: 0.0) }
        return formulaEngine.calculateTgpa(mappedSubjects, config).map { tgpa ->
            TgpaResult(universityId = universityId, universityName = config.universityName,
                semesterName = semesterName, tgpa = tgpa, totalCredits = subjects.sumOf { it.credits },
                totalSubjects = subjects.size, percentage = formulaEngine.cgpaToPercentage(tgpa, config), subjects = mappedSubjects)
        }
    }

    suspend fun saveResult(result: TgpaResult): Long = resultRepository.saveTgpaResult(result)
}
