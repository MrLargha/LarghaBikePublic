package ru.mrlargha.larghabike.model.domain

/**
 * Поездка и связанные с ней кадры телеметрии
 *
 * @property ride поездка
 * @property frames список её [TelemetryFrame]
 */
data class RideWithFrames(val ride: Ride, val frames: List<TelemetryFrame>)