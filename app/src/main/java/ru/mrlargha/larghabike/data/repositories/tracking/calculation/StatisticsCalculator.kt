package ru.mrlargha.larghabike.data.repositories.tracking.calculation

import ru.mrlargha.larghabike.model.domain.StatisticFrame
import ru.mrlargha.larghabike.model.domain.TelemetryFrame
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Калькулятор статистики
 */
@Singleton
class StatisticsCalculator @Inject constructor() {
    private var distance: Float = 0f
    private var maxSpeed: Float = Float.MIN_VALUE
    private var rideTime: Long = 0
    private var lastKilometerPace = 0L
    private var lastKilometerPaceKilometer = 0
    private var startTime = Date()
    private var lastKilometerTime = Date()
    private var lastTime = Date()
    private var lastFrame: TelemetryFrame? = null

    private var lastStatisticFrame: StatisticFrame? = null

    /**
     * Добавить кадр телеметрии для подсчёта статистики
     *
     * @param frame кадр телеметрии для обработки
     * @return новый кадр статистики
     */
    fun addFrame(frame: TelemetryFrame): StatisticFrame {
        if (((distance + frame.distanceDelta) / 1000).toInt() > lastKilometerPaceKilometer) {
            lastKilometerPace = frame.time.time - lastKilometerTime.time
            lastKilometerTime = frame.time
            distance += frame.distanceDelta
            lastKilometerPaceKilometer = (distance / 1000).toInt()
        } else {
            distance += frame.distanceDelta
        }

        if (frame.currentSpeed > maxSpeed) {
            maxSpeed = frame.currentSpeed
        }

        lastFrame?.let {
            if (it.currentSpeed != 0f) {
                rideTime += frame.time.time - it.time.time
            }
        }

        lastFrame = frame

        val totalTime = frame.time.time - startTime.time

        return StatisticFrame(
            date = Date(),
            totalDistance = distance,
            maxSpeed = maxSpeed,
            averageSpeed = calculateAverageSpeed(distance, totalTime),
            averageMovingSpeed = calculateAverageSpeed(distance, rideTime),
            totalTime = totalTime,
            movingTime = rideTime,
            lastKilometerPace = lastKilometerPace
        ).also { lastStatisticFrame = it }
    }

    /**
     * Метод для пропуска добавления кадра телеметрии для того чтобы просто увеличивалось
     * число на таймере
     *
     * @return кадр статистики с обновлёнными таймерами
     */
    fun addEmptyFrame(): StatisticFrame {
        val totalTime = Date().time - startTime.time
        return StatisticFrame(
            date = Date(),
            totalDistance = distance,
            maxSpeed = maxSpeed,
            averageSpeed = calculateAverageSpeed(distance, totalTime),
            averageMovingSpeed = calculateAverageSpeed(distance, rideTime),
            totalTime = totalTime,
            movingTime = rideTime,
            lastKilometerPace = lastKilometerPace
        ).also { lastStatisticFrame = it }
    }

    /**
     * Получить последний кадр статистики
     *
     * @return последний кадр статистики
     */
    fun getLastStatisticFrame() = lastStatisticFrame

    /**
     * Сбросить состояние калькулятора статистики
     */
    fun reset() {
        distance = 0f
        maxSpeed = Float.MIN_VALUE
        rideTime = 0
        lastTime = Date()
        lastKilometerPace = 0
        lastKilometerPaceKilometer = 0
        lastKilometerTime = Date()
        startTime = Date()
        lastFrame = null
    }

    private fun calculateAverageSpeed(distance: Float, time: Long): Float {
        val avSpeed = distance / (time / 1000)
        if (!avSpeed.isFinite()) {
            return 0f
        }
        return avSpeed
    }

}