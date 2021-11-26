package ru.mrlargha.larghabike.model.presentation

import com.google.android.gms.maps.model.LatLng
import ru.mrlargha.larghabike.model.domain.StatisticFrame
import ru.mrlargha.larghabike.model.domain.TelemetryFrame

/**
 * Данные трекера и путь
 *
 * @property telemetryFrame кадр телеметрии
 * @property statisticFrame кадр статистики
 * @property path путь
 */
data class TrackerDataWithPath(
    val telemetryFrame: TelemetryFrame?,
    val statisticFrame: StatisticFrame?,
    val path: List<LatLng>?
)