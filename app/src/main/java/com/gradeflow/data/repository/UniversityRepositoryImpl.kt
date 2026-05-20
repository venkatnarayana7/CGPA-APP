package com.gradeflow.data.repository

import android.content.Context
import com.gradeflow.data.local.dao.ResultDao
import com.gradeflow.data.local.entity.FavoriteUniversityEntity
import com.gradeflow.data.model.UniversityConfig
import com.gradeflow.domain.model.University
import com.gradeflow.domain.repository.UniversityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UniversityRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resultDao: ResultDao
) : UniversityRepository {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private var cache: Map<String, UniversityConfig>? = null

    private suspend fun loadAll(): Map<String, UniversityConfig> {
        cache?.let { return it }
        return withContext(Dispatchers.IO) {
            val configs = mutableMapOf<String, UniversityConfig>()
            try {
                val files = context.assets.list("universities") ?: emptyArray()
                for (f in files) {
                    if (f.endsWith(".json")) {
                        try {
                            val text = context.assets.open("universities/$f").bufferedReader().use { it.readText() }
                            val config = json.decodeFromString<UniversityConfig>(text)
                            configs[config.universityId] = config
                        } catch (_: Exception) {}
                    }
                }
            } catch (_: Exception) {}
            cache = configs; configs
        }
    }

    override suspend fun getAllUniversities(): List<University> = loadAll().values.map { c ->
        University(c.universityId, c.universityName, c.shortName, c.location, c.gradingScale, resultDao.isFavorite(c.universityId))
    }.sortedBy { it.name }

    override suspend fun getUniversityConfig(universityId: String): UniversityConfig? = loadAll()[universityId]

    override suspend fun searchUniversities(query: String): List<University> {
        if (query.isBlank()) return getAllUniversities()
        return loadAll().values.filter {
            it.universityName.contains(query, true) || it.shortName.contains(query, true) || it.location.contains(query, true)
        }.map { c -> University(c.universityId, c.universityName, c.shortName, c.location, c.gradingScale, resultDao.isFavorite(c.universityId)) }
    }

    override fun getFavoriteUniversities(): Flow<List<University>> = resultDao.getAllFavoriteUniversities().map { favs ->
        favs.map { f -> val c = loadAll()[f.universityId]; University(f.universityId, f.universityName, f.shortName, c?.location ?: "", c?.gradingScale ?: 10, true) }
    }

    override suspend fun toggleFavorite(universityId: String, universityName: String, shortName: String) {
        if (resultDao.isFavorite(universityId)) resultDao.deleteFavoriteUniversity(FavoriteUniversityEntity(universityId, universityName, shortName))
        else resultDao.insertFavoriteUniversity(FavoriteUniversityEntity(universityId, universityName, shortName))
    }

    override suspend fun isFavorite(universityId: String): Boolean = resultDao.isFavorite(universityId)
}
