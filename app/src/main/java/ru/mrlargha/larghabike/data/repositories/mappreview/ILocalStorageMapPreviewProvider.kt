package ru.mrlargha.larghabike.data.repositories.mappreview

import android.graphics.Bitmap
import io.reactivex.Completable
import io.reactivex.Single
import ru.mrlargha.larghabike.model.domain.Ride

/**
 * Локальный провайдер закэшированных картинок с маршрутом
 */

interface ILocalStorageMapPreviewProvider {
    /**
     * Получить карту с маршрутом для указанной поздки
     *
     * @param ride поездка для получения карты
     * @return  [Single] с Bitmap с картой маршрута
     */
    fun getMapPreview(ride: Ride): Single<Bitmap>

    /**
     * Проверить наличие в локальном хранилище карты на указанный маршрут
     *
     * @param rideId  id поездки
     * @return true если имеется карта для поездки, false - если нет
     */
    fun hasPreviewForRide(rideId: Long): Boolean

    /**
     * Сохранить карту в локально хранилище
     *
     * @param rideId id поездки
     * @param bitmap картинка с картой
     *
     * @return [Completable]
     */
    fun saveMap(rideId: Long, bitmap: Bitmap): Completable
}