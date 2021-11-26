package ru.mrlargha.larghabike.data.repositories.mappreview

import android.graphics.Bitmap
import io.reactivex.Single
import ru.mrlargha.larghabike.domain.repos.IMapPreviewRepository
import ru.mrlargha.larghabike.model.domain.Ride
import ru.mrlargha.larghabike.model.domain.TelemetryFrame
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация репозитория для работы с превью маршрута на карте
 *
 * @property networkMapPreviewProvider провайдер карт по сети
 * @property localMapPreviewProvider локальный провайдер карт (используется как кэш)
 */
@Singleton
class MapPreviewRepository @Inject constructor(
    private val networkMapPreviewProvider: INetworkMapPreviewProvider,
    private val localMapPreviewProvider: ILocalStorageMapPreviewProvider
) : IMapPreviewRepository {

    override fun getMapForRide(ride: Ride, frames: List<TelemetryFrame>): Single<Bitmap> {
        return if (localMapPreviewProvider.hasPreviewForRide(ride.id)) {
            localMapPreviewProvider.getMapPreview(ride)
        } else {
            networkMapPreviewProvider.getPreview(
                frames.mapNotNull { frame -> frame.getLatLng() },
                600,
                600
            ).doOnSuccess {
                localMapPreviewProvider.saveMap(ride.id, it).subscribe()
            }
        }
    }
}