package com.gradeflow.domain.repository

import com.gradeflow.domain.model.CgpaResult
import com.gradeflow.domain.model.TgpaResult
import kotlinx.coroutines.flow.Flow

interface ResultRepository {
    suspend fun saveTgpaResult(result: TgpaResult): Long
    fun getAllTgpaResults(): Flow<List<TgpaResult>>
    suspend fun deleteTgpaResult(id: Long)
    suspend fun deleteAllTgpaResults()
    suspend fun saveCgpaResult(result: CgpaResult): Long
    fun getAllCgpaResults(): Flow<List<CgpaResult>>
    suspend fun deleteCgpaResult(id: Long)
    suspend fun deleteAllCgpaResults()
}
