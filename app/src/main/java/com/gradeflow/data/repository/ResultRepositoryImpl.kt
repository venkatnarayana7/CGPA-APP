package com.gradeflow.data.repository

import com.gradeflow.data.local.dao.ResultDao
import com.gradeflow.data.local.entity.CgpaResultEntity
import com.gradeflow.data.local.entity.TgpaResultEntity
import com.gradeflow.domain.model.CgpaResult
import com.gradeflow.domain.model.SemesterEntry
import com.gradeflow.domain.model.SubjectEntry
import com.gradeflow.domain.model.TgpaResult
import com.gradeflow.domain.repository.ResultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable private data class SubjectData(val name: String, val credits: Int, val grade: String, val gradePoints: Double)
@Serializable private data class SemesterData(val name: String, val gpa: Double, val credits: Int)

@Singleton
class ResultRepositoryImpl @Inject constructor(private val resultDao: ResultDao) : ResultRepository {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun saveTgpaResult(result: TgpaResult): Long {
        return resultDao.insertTgpaResult(TgpaResultEntity(
            universityId = result.universityId, universityName = result.universityName,
            semesterName = result.semesterName, tgpa = result.tgpa, totalCredits = result.totalCredits,
            totalSubjects = result.totalSubjects, percentage = result.percentage,
            subjects = json.encodeToString(result.subjects.map { SubjectData(it.name, it.credits, it.grade, it.gradePoints) }),
            timestamp = result.timestamp
        ))
    }

    override fun getAllTgpaResults(): Flow<List<TgpaResult>> = resultDao.getAllTgpaResults().map { list ->
        list.map { e ->
            val subjects = try { json.decodeFromString<List<SubjectData>>(e.subjects).map { SubjectEntry(name = it.name, credits = it.credits, grade = it.grade, gradePoints = it.gradePoints) } } catch (_: Exception) { emptyList() }
            TgpaResult(e.id, e.universityId, e.universityName, e.semesterName, e.tgpa, e.totalCredits, e.totalSubjects, e.percentage, subjects, e.timestamp)
        }
    }

    override suspend fun deleteTgpaResult(id: Long) { resultDao.getTgpaResultById(id)?.let { resultDao.deleteTgpaResult(it) } }
    override suspend fun deleteAllTgpaResults() { resultDao.deleteAllTgpaResults() }

    override suspend fun saveCgpaResult(result: CgpaResult): Long {
        return resultDao.insertCgpaResult(CgpaResultEntity(
            universityId = result.universityId, universityName = result.universityName,
            cgpa = result.cgpa, totalSemesters = result.totalSemesters, totalCredits = result.totalCredits,
            percentage = result.percentage,
            semesters = json.encodeToString(result.semesters.map { SemesterData(it.semesterName, it.gpa, it.credits) }),
            timestamp = result.timestamp
        ))
    }

    override fun getAllCgpaResults(): Flow<List<CgpaResult>> = resultDao.getAllCgpaResults().map { list ->
        list.map { e ->
            val semesters = try { json.decodeFromString<List<SemesterData>>(e.semesters).map { SemesterEntry(semesterName = it.name, gpa = it.gpa, credits = it.credits) } } catch (_: Exception) { emptyList() }
            CgpaResult(e.id, e.universityId, e.universityName, e.cgpa, e.totalSemesters, e.totalCredits, e.percentage, semesters, e.timestamp)
        }
    }

    override suspend fun deleteCgpaResult(id: Long) { resultDao.getCgpaResultById(id)?.let { resultDao.deleteCgpaResult(it) } }
    override suspend fun deleteAllCgpaResults() { resultDao.deleteAllCgpaResults() }
}
