package com.gradeflow.di

import android.content.Context
import androidx.room.Room
import com.gradeflow.data.local.dao.ResultDao
import com.gradeflow.data.local.database.GradeFlowDatabase
import com.gradeflow.data.repository.ResultRepositoryImpl
import com.gradeflow.data.repository.UniversityRepositoryImpl
import com.gradeflow.domain.repository.ResultRepository
import com.gradeflow.domain.repository.UniversityRepository
import com.gradeflow.engine.FormulaEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GradeFlowDatabase =
        Room.databaseBuilder(context, GradeFlowDatabase::class.java, "gradeflow_database").fallbackToDestructiveMigration().build()

    @Provides @Singleton
    fun provideResultDao(db: GradeFlowDatabase): ResultDao = db.resultDao()

    @Provides @Singleton
    fun provideFormulaEngine(): FormulaEngine = FormulaEngine()

    @Provides @Singleton
    fun provideUniversityRepository(@ApplicationContext context: Context, dao: ResultDao): UniversityRepository = UniversityRepositoryImpl(context, dao)

    @Provides @Singleton
    fun provideResultRepository(dao: ResultDao): ResultRepository = ResultRepositoryImpl(dao)
}
