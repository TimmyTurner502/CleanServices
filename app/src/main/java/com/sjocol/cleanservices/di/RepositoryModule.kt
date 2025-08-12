package com.sjocol.cleanservices.di

import com.sjocol.cleanservices.data.repository.HouseRepository
import com.sjocol.cleanservices.data.repository.HouseRepositoryImpl
import com.sjocol.cleanservices.data.repository.WorkEntryRepository
import com.sjocol.cleanservices.data.repository.WorkEntryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHouseRepository(impl: HouseRepositoryImpl): HouseRepository

    @Binds
    @Singleton
    abstract fun bindWorkEntryRepository(impl: WorkEntryRepositoryImpl): WorkEntryRepository
} 