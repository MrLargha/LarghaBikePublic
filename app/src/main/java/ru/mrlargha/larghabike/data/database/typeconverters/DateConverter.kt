package ru.mrlargha.larghabike.data.database.typeconverters

import androidx.room.TypeConverter
import java.util.*

/**
 * Конвертер для перевода объектов [Date] в [Long]
 */
class DateConverter {
    /**
     * Переводит UnixTimestamp в [Date]
     *
     * @param value дата в формате Unix timestamp
     * @return объект [Date] или `null`
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Переводит [Date] в Unix timestamp
     *
     * @param date объект [Date]
     * @return Unix timestamp или `null`
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}