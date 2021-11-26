package ru.mrlargha.larghabike.model.domain

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.util.*

/**
 * Модель представляющая кадр телеметрии
 *
 * @property id
 * @property rideId ID поездки
 * @property time Время записи кадра
 * @property currentSpeed Текущая скорость
 * @property distanceDelta Пройденное расстояние с последнего кадра
 * @property rotationsPerMinute Обороты колеса в минуту
 * @property currentCadence  Каденция (может отсутствовать, если не был подключён датчик)
 * @property latitude latitude (может отсутствовать, если не был доступен GPS)
 * @property longitude longitude (может отсутствовать, если не был доступен GPS)
 */
data class TelemetryFrame(
    val id: Int = 0,
    val rideId: Long,
    val time: Date,
    val currentSpeed: Float,
    val distanceDelta: Float,
    val rotationsPerMinute: Int? = null,
    val currentCadence: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    /**
     * Генерирует объект [LatLng] на базе данных в кадре телеметрии
     *
     * @return [LatLng] или null, если GPS не был доступен на момент записи данного кадра
     */
    fun getLatLng(): LatLng? {
        return if (latitude == null || longitude == null) {
            null
        } else {
            LatLng(latitude, longitude)
        }
    }

    /**
     * Генерирует объект [Location] на базе данного кадра телеметри
     *
     * @return [Location] или null, если GPS не был доступен на момент записи данного кадра
     */
    fun getLocation(): Location? {
        return if (latitude == null || longitude == null) {
            null
        } else {
            Location("").also {
                it.longitude = longitude
                it.latitude = latitude
            }
        }
    }
}

