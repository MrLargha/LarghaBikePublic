package ru.mrlargha.larghabike.data.repositories.tracking.calculation

import android.location.Location

/**
 * Фильтр для проверки локации, предназначен для отсечения неправильной и нежелательной телеметрии
 */
object GPSLocationFilter {
    private const val MINIMAL_SPEED = 1f

    /**
     * Фильтрует локацию по минимальной скорости
     *
     * @param location локация для фильтрации
     * @return отфильтрованная локация
     */
    fun filterLocation(location: Location): Location {
        if (location.speed < MINIMAL_SPEED) {
            location.speed = 0f
        }
        return location
    }
}