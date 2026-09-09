package com.winsome.nuzul.di

import com.winsome.nuzul.data.repository.BookingsRepositoryImpl
import com.winsome.nuzul.data.repository.FavoritesRepositoryImpl
import com.winsome.nuzul.data.repository.HotelsRepositoryImpl
import com.winsome.nuzul.domain.repository.BookingsRepository
import com.winsome.nuzul.domain.repository.FavoritesRepository
import com.winsome.nuzul.domain.repository.HotelsRepository
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
    abstract fun bindHotelsRepository(
        impl: HotelsRepositoryImpl
    ): HotelsRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(
        impl: FavoritesRepositoryImpl
    ): FavoritesRepository

    @Binds
    @Singleton
    abstract fun bindBookingsRepository(
        impl: BookingsRepositoryImpl
    ): BookingsRepository
}