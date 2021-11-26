package ru.mrlargha.larghabike.data.repositories.tracking.calculation

import ru.mrlargha.larghabike.model.domain.Bike

/**
 * Утилита для выполнения различных рассчётов на базе GPS
 */
object GPSCalculationUtil {
    /**
     * Рассчитывает обороты в минуту
     *
     * @param distance расстояние
     * @param time время в миллисекундах, за которое было пройдено расстояние
     * @param bike велосипед, на котором это было сделано
     * @return обороты в минуты
     */
    fun calculateRPM(distance: Float, time: Long, bike: Bike): Float {
        val wheelLength = inchesToMeters(bike.wheelDiameter) * Math.PI
        val rotations = distance / wheelLength
        val rpm = ((rotations / time) * 60 * 1000).toFloat()
        if (!rpm.isFinite()) {
            return 0f
        }
        return rpm
    }

    private fun inchesToMeters(inches: Float): Float {
        return inches / 39.37f
    }
}