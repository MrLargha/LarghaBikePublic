package ru.mrlargha.larghabike.model.converters

import ru.mrlargha.larghabike.model.data.RideWithFramesEntity
import ru.mrlargha.larghabike.model.domain.RideWithFrames

class RideWithFramesToRideWithFramesEntityConverter(
    private val rideConverter: RideToRideEntityConverter,
    private val telemetryFrameConverter: TelemetryFrameToTelemetryFrameEntityConverter
) :
    TwoWayConverter<RideWithFrames, RideWithFramesEntity> {
    override fun convert(from: RideWithFrames): RideWithFramesEntity {
        return RideWithFramesEntity(
            from.ride.let(rideConverter::convert),
            from.frames.map(telemetryFrameConverter::convert)
        )
    }

    override fun convertReverse(from: RideWithFramesEntity): RideWithFrames {
        return RideWithFrames(
            from.ride.let(rideConverter::convertReverse),
            from.frames.map(telemetryFrameConverter::convertReverse)
        )
    }
}