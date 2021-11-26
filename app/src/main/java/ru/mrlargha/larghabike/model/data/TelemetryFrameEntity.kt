package ru.mrlargha.larghabike.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Entity представляющая кадр телеметрии в базе данных
 *
 * @property id а эт что такое?
 * @property rideId ID поездки
 * @property time Время записи кадра
 * @property currentSpeed Текущая скорость
 * @property distanceDelta Пройденное расстояние с последнего кадра
 * @property rotationsPerMinute Обороты колеса в минуту
 * @property currentCadence  Каденция (может отсутствовать, если не был подключён датчик)
 * @property latitude latitude (может отсутствовать, если не был доступен GPS)
 * @property longitude longitude (может отсутствовать, если не был доступен GPS)
 */
@Entity(tableName = "telemetry_frame")
data class TelemetryFrameEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rideId: Long,
    val time: Date,
    val currentSpeed: Float,
    val distanceDelta: Float,
    val rotationsPerMinute: Int? = null,
    val currentCadence: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

