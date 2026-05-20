package com.gradeflow.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gradeflow.data.local.dao.ResultDao
import com.gradeflow.data.local.entity.CgpaResultEntity
import com.gradeflow.data.local.entity.FavoriteUniversityEntity
import com.gradeflow.data.local.entity.TgpaResultEntity

@Database(
    entities = [TgpaResultEntity::class, CgpaResultEntity::class, FavoriteUniversityEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GradeFlowDatabase : RoomDatabase() {
    abstract fun resultDao(): ResultDao
}
