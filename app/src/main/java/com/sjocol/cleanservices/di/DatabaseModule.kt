package com.sjocol.cleanservices.di

import android.content.Context
import androidx.room.Room
import com.sjocol.cleanservices.data.local.AppDatabase
import com.sjocol.cleanservices.data.local.dao.HouseDao
import com.sjocol.cleanservices.data.local.dao.WorkEntryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "clean_services.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideHouseDao(db: AppDatabase): HouseDao = db.houseDao()

    @Provides
    fun provideWorkEntryDao(db: AppDatabase): WorkEntryDao = db.workEntryDao()
} 