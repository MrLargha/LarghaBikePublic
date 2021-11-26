package ru.mrlargha.larghabike.data.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat

/**
 * Форматировщики для различных величин
 */
object Formatters {
    /**
     * Стандартный формат для чесел
     */
    val defaultNumberFormat = DecimalFormat("0.0")

    /**
     * Стандартный формат для даты и времени
     */
    val defaultDateTimeFormat = SimpleDateFormat("dd.MM.yyyy hh:mm")

    /**
     * Форматировать время в миллисекундах в часы и минуты
     *
     * @param time длительность в миллисекундах
     * @return длительность в формате "*1h 2m*"
     */
    fun formatTimeHoursMinutes(time: Long): String {
        val hours = time / (1000 * 60 * 60)
        val minutes = (time - (hours * (1000 * 60 * 60))) / (1000 * 60)
        return "${hours}h ${minutes}m"
    }
}