package ru.mrlargha.larghabike.model.converters

import ru.mrlargha.larghabike.model.data.TelemetryFrameEntity
import ru.mrlargha.larghabike.model.domain.TelemetryFrame

class TelemetryFrameToTelemetryFrameEntityConverter :
    TwoWayConverter<TelemetryFrame, TelemetryFrameEntity> {
    override fun convert(from: TelemetryFrame): TelemetryFrameEntity {
        return TelemetryFrameEntity(
            from.id,
            from.rideId,
            from.time,
            from.currentSpeed,
            from.distanceDelta,
            from.rotationsPerMinute,
            from.currentCadence,
            from.latitude,
            from.longitude
        )
    }

    override fun convertReverse(from: TelemetryFrameEntity): TelemetryFrame {
        return TelemetryFrame(
            from.id,
            from.rideId,
            from.time,
            from.currentSpeed,
            from.distanceDelta,
            from.rotationsPerMinute,
            from.currentCadence,
            from.latitude,
            from.longitude
        )
    }
}