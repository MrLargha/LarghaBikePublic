package ru.mrlargha.larghabike.model.domain

import java.util.*

/**
 * Модель представляющая поездку
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
 */
data class Ride(
    val id: Long = 0,
    val startTime: Date = Date(),
    var endTime: Date = Date(),
    var stopSeconds: Int = 0,
    var maxSpeed: Float = 0f,
    var avSpeed: Float = 0f,
    var avMoveSpeed: Float = 0f,
    var distance: Float = 0f,
    var completed: Boolean = false,
    var bikeId: Long = -1L,
    var isCurrentRide: Boolean = false
) {
    /**
     * Устанавливает поля в соответсвии с данным в переданном [StatisticFrame]
     *
     * @param statisticFrame кадр статистики
     */
    fun setDataFromStatisticFrame(statisticFrame: StatisticFrame) {
        endTime = Date(startTime.time + statisticFrame.totalTime)
        stopSeconds = ((statisticFrame.totalTime - statisticFrame.movingTime) / 1000).toInt()
        maxSpeed = statisticFrame.maxSpeed
        avSpeed = statisticFrame.averageSpeed
        avMoveSpeed = statisticFrame.averageMovingSpeed
        distance = statisticFrame.totalDistance
        completed = true
    }

    /**
     * Рассчитывает длительноость поездки
     *
     * @return длительность поездки в минутах
     */
    fun getDurationMinutes(): Int = ((endTime.time - startTime.time) / 1000 / 60).toInt()

    /**
     * Рассчитывает время в движении
     *
     * @return время в секундах
     */
    fun getMoveTimeMinutes(): Int =
        (((endTime.time - startTime.time) / 1000 - stopSeconds) / 60).toInt()
}