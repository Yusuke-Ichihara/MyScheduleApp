package com.example.myscheduleapp.di

import android.content.Context
import androidx.room.Room
import com.example.myscheduleapp.data.AppDatabase
import com.example.myscheduleapp.data.ScheduleDao
import com.example.myscheduleapp.data.ScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideScheduleDao(appDatabase: AppDatabase): ScheduleDao {
        return appDatabase.scheduleDao()
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(scheduleDao: ScheduleDao): ScheduleRepository {
        return ScheduleRepository(scheduleDao)
    }

}