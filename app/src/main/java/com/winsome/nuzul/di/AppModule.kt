package com.winsome.nuzul.di

import android.content.Context
import androidx.room.Room
import com.winsome.nuzul.data.local.NuzulDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true // Prevents crashes if JSON has extra fields
        coerceInputValues = true // Coerces nulls to default values
    }

    @Provides
    @Singleton
    fun provideNuzulDatabase(
        @ApplicationContext context: Context
    ): NuzulDatabase = Room.databaseBuilder(
        context,
        NuzulDatabase::class.java,
        "nuzul.db"
    ).fallbackToDestructiveMigration(false).build()
}