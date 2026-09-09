package com.winsome.nuzul.di

import com.winsome.nuzul.data.local.NuzulDatabase
import com.winsome.nuzul.data.local.dao.BookingDao
import com.winsome.nuzul.data.local.dao.FavoriteDao
import com.winsome.nuzul.data.local.dao.HotelDao
import com.winsome.nuzul.data.remote.HotelsDataSource
import com.winsome.nuzul.data.remote.MockHotelsDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
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

@Module
@InstallIn(SingletonComponent::class)
object DataSourceDaoModule {

    @Provides
    fun provideHotelDao(database: NuzulDatabase): HotelDao = database.hotelDao()

    @Provides
    fun provideFavoriteDao(database: NuzulDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideBookingDao(database: NuzulDatabase): BookingDao = database.bookingDao()
}