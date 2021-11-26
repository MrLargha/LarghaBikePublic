package ru.mrlargha.larghabike.model.converters

import ru.mrlargha.larghabike.model.data.RideEntity
import ru.mrlargha.larghabike.model.domain.Ride

class RideToRideEntityConverter :
    TwoWayConverter<Ride, RideEntity> {
    override fun convert(from: Ride): RideEntity {
        return RideEntity(
            from.id,
            from.startTime,
            from.endTime,
            from.stopSeconds,
            from.maxSpeed,
            from.avSpeed,
            from.avMoveSpeed,
            from.distance,
            from.completed,
            from.bikeId,
            from.isCurrentRide
        )
    }

    override fun convertReverse(from: RideEntity): Ride {
        return Ride(
            from.id,
            from.startTime,
            from.endTime,
            from.stopSeconds,
            from.maxSpeed,
            from.avSpeed,
            from.avMoveSpeed,
            from.distance,
            from.completed,
            from.bikeId,
            from.isCurrentRide
        )
    }
}