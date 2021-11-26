package ru.mrlargha.larghabike.domain.repos

import android.graphics.Bitmap
import io.reactivex.Single
import ru.mrlargha.larghabike.model.domain.Ride
import ru.mrlargha.larghabike.model.domain.TelemetryFrame

/**
 * Репозиторий для работы с превью маршрута поедки на карте
 *
 */
interface IMapPreviewRepository {
    /**
     * Получить картинку с картой и маршрутом для поездки
     *
     * @param ride поездка
     * @param frames кадры телеметрии
     * @return [Single] с картинкой
     */
    fun getMapForRide(ride: Ride, frames: List<TelemetryFrame>): Single<Bitmap>
}