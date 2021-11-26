package ru.mrlargha.larghabike.model.domain

/**
 * Комплексная модель данных от трэкера
 *
 * @property telemetryFrame кадр телеметрии
 * @property statisticFrame кадр статистики
 */
data class TrackerData(val telemetryFrame: TelemetryFrame?, val statisticFrame: StatisticFrame?)