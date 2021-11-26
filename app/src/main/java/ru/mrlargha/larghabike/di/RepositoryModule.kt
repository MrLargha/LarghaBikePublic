package ru.mrlargha.larghabike.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.mrlargha.larghabike.data.repositories.bike.BikeRepository
import ru.mrlargha.larghabike.data.repositories.mappreview.*
import ru.mrlargha.larghabike.data.repositories.ride.DatabaseRideRepository
import ru.mrlargha.larghabike.data.repositories.tracking.TrackingRepository
import ru.mrlargha.larghabike.data.util.APIKeysProvider
import ru.mrlargha.larghabike.data.util.FilesystemProvider
import ru.mrlargha.larghabike.data.util.URLRepository
import ru.mrlargha.larghabike.domain.repos.*

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindBikeRepository(bikeRepository: BikeRepository): IBikeRepository

    @Binds
    abstract fun bindMapPreviewRepository(mapPreviewRepository: MapPreviewRepository): IMapPreviewRepository

    @Binds
    abstract fun bindRideRepository(rideRepository: DatabaseRideRepository): IRideRepository

    @Binds
    abstract fun bindTrackingRepository(trackingRepository: TrackingRepository): ITrackingRepository

    @Binds
    abstract fun bindLocalMapProvider(localStorageMapPreviewProvider: LocalStorageMapPreviewProvider): ILocalStorageMapPreviewProvider

    @Binds
    abstract fun bindNetworkMapProvider(networkMapPreviewProvider: NetworkMapPreviewProvider): INetworkMapPreviewProvider

    @Binds
    abstract fun bindApiKeysProvider(apiKeysProvider: APIKeysProvider): IAPIKeysProvider

    @Binds
    abstract fun bindFilesystemProvider(fileSystemProvider: FilesystemProvider): IFilesystemProvider

    @Binds
    abstract fun bindUrlRepository(urlRepository: URLRepository): IURLRepository
}