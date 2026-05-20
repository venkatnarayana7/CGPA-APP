package com.gradeflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tgpa_results")
data class TgpaResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val universityId: String,
    val universityName: String,
    val semesterName: String,
    val tgpa: Double,
    val totalCredits: Int,
    val totalSubjects: Int,
    val percentage: Double,
    val subjects: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cgpa_results")
data class CgpaResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val universityId: String,
    val universityName: String,
    val cgpa: Double,
    val totalSemesters: Int,
    val totalCredits: Int,
    val percentage: Double,
    val semesters: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_universities")
data class FavoriteUniversityEntity(
    @PrimaryKey val universityId: String,
    val universityName: String,
    val shortName: String,
    val timestamp: Long = System.currentTimeMillis()
)
