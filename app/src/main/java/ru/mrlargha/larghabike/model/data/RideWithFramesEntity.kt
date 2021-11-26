package ru.mrlargha.larghabike.model.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Поездка и связанные с ней кадры телеметрии
 *
 * @property ride поездка
 * @property frames список её [TelemetryFrameEntity]
 */
data class RideWithFramesEntity(
    @Embedded
    val ride: RideEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "rideId"
    )
    val frames: List<TelemetryFrameEntity>
)