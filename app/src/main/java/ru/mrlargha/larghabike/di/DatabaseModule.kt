package ru.mrlargha.larghabike.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.mrlargha.larghabike.data.database.BikeDAO
import ru.mrlargha.larghabike.data.database.RideDAO
import ru.mrlargha.larghabike.data.database.RideDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideRideDAO(database: RideDatabase): RideDAO {
        return database.rideDao()
    }

    @Singleton
    @Provides
    fun provideBikeDAO(database: RideDatabase): BikeDAO {
        return database.bikeDao()
    }

    @Singleton
    @Provides
    fun provideRideDatabase(@ApplicationContext appContext: Context): RideDatabase {
        return Room.databaseBuilder(
            appContext,
            RideDatabase::class.java,
            "RideDatabase"
        ).fallbackToDestructiveMigration().build()
    }
}