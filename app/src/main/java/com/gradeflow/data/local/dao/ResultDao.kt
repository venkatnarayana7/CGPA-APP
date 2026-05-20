package com.gradeflow.data.local.dao

import androidx.room.*
import com.gradeflow.data.local.entity.CgpaResultEntity
import com.gradeflow.data.local.entity.FavoriteUniversityEntity
import com.gradeflow.data.local.entity.TgpaResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTgpaResult(result: TgpaResultEntity): Long

    @Query("SELECT * FROM tgpa_results ORDER BY timestamp DESC")
    fun getAllTgpaResults(): Flow<List<TgpaResultEntity>>

    @Query("SELECT * FROM tgpa_results WHERE id = :id")
    suspend fun getTgpaResultById(id: Long): TgpaResultEntity?

    @Delete
    suspend fun deleteTgpaResult(result: TgpaResultEntity)

    @Query("DELETE FROM tgpa_results")
    suspend fun deleteAllTgpaResults()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCgpaResult(result: CgpaResultEntity): Long

    @Query("SELECT * FROM cgpa_results ORDER BY timestamp DESC")
    fun getAllCgpaResults(): Flow<List<CgpaResultEntity>>

    @Query("SELECT * FROM cgpa_results WHERE id = :id")
    suspend fun getCgpaResultById(id: Long): CgpaResultEntity?

    @Delete
    suspend fun deleteCgpaResult(result: CgpaResultEntity)

    @Query("DELETE FROM cgpa_results")
    suspend fun deleteAllCgpaResults()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteUniversity(university: FavoriteUniversityEntity)

    @Query("SELECT * FROM favorite_universities ORDER BY timestamp DESC")
    fun getAllFavoriteUniversities(): Flow<List<FavoriteUniversityEntity>>

    @Delete
    suspend fun deleteFavoriteUniversity(university: FavoriteUniversityEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_universities WHERE universityId = :universityId)")
    suspend fun isFavorite(universityId: String): Boolean
}
