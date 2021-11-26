package ru.mrlargha.larghabike.model.converters

import ru.mrlargha.larghabike.model.data.BikeEntity
import ru.mrlargha.larghabike.model.domain.Bike

class FromBikeToBikeEntity : TwoWayConverter<Bike, BikeEntity> {
    override fun convert(from: Bike): BikeEntity {
        return BikeEntity(from.id, from.name, from.wheelDiameter, from.photoPath)
    }

    override fun convertReverse(from: BikeEntity): Bike {
        return Bike(from.id, from.name, from.wheelDiameter, from.photoPath)
    }
}