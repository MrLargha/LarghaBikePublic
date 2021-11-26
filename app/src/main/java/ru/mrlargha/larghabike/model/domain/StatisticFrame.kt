package ru.mrlargha.larghabike.model.domain

import java.util.*

/**
 * Кадр статистической информации
 */
data class StatisticFrame(
    /**
     * Время подготовки кадра статистики
     */
    val date: Date,

    /**
     * Общая пройденная дистанция в метрах
     */
    val totalDistance: Float,

    /**
     * Максимальная скорость
     */
    val maxSpeed: Float,

    /**
     * Среднаяя скрость
     */
    val averageSpeed: Float,

    /**
     * Средняя скорость движения (без учёта времени остановок)
     */
    val averageMovingSpeed: Float,

    /**
     * Общее время со старта отслеживания в миллисекундах
     */
    val totalTime: Long,

    /**
     * Время в пути (только когда телефон пермещался)
     */
    val movingTime: Long,

    /**
     * Время в миллисекундах, за которое пройден последний километр
     */
    val lastKilometerPace: Long,
)