package ru.mrlargha.larghabike.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Entity представляющая поездку в базе данных
 *
 * @property id id поездки
 * @property startTime время начала
 * @property endTime время окончания
 * @property stopSeconds секунды без движения, стоя на месте
 * @property maxSpeed максмальная скорость
 * @property avSpeed средняя скорость
 * @property avMoveSpeed средняя скорость движения (без учёта времени в простое)
 * @property distance пройденная дистанция
 * @property completed поездка завершена
 * @property bikeId id велосипеда на котором совершена поездка
 * @property isCurrentRide является ли текущей поездкой
 */
@Entity(tableName = "ride")
data class RideEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Date = Date(),
    val endTime: Date = Date(),
    val stopSeconds: Int = 0,
    val maxSpeed: Float = 0f,
    val avSpeed: Float = 0f,
    val avMoveSpeed: Float = 0f,
    val distance: Float = 0f,
    val completed: Boolean = false,
    val bikeId: Long = -1L,
    val isCurrentRide: Boolean = false

)