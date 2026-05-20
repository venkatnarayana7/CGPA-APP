package com.gradeflow.domain.repository

import com.gradeflow.data.model.UniversityConfig
import com.gradeflow.domain.model.University
import kotlinx.coroutines.flow.Flow

interface UniversityRepository {
    suspend fun getAllUniversities(): List<University>
    suspend fun getUniversityConfig(universityId: String): UniversityConfig?
    suspend fun searchUniversities(query: String): List<University>
    fun getFavoriteUniversities(): Flow<List<University>>
    suspend fun toggleFavorite(universityId: String, universityName: String, shortName: String)
    suspend fun isFavorite(universityId: String): Boolean
}
