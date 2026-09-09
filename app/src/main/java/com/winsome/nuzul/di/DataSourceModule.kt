package com.winsome.nuzul.di

import com.winsome.nuzul.data.remote.HotelsDataSource
import com.winsome.nuzul.data.remote.MockHotelsDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindHotelsDataSource(
        impl: MockHotelsDataSource
    ): HotelsDataSource
}