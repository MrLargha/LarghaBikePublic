package ru.mrlargha.larghabike.model.presentation.ridestat

import ru.mrlargha.larghabike.model.presentation.LineChartEntry

/**
 * Данные для карточки с графиком скорости
 *
 * @property name заголовок
 * @property description описание
 * @property mainChartData основной график
 * @property avSpeedBaselineData линия средней скорости
 */
data class SpeedChartCardData(
    val name: String,
    val description: String,
    val mainChartData: List<LineChartEntry>,
    val avSpeedBaselineData: List<LineChartEntry>,
) : AbstractRideDetailCardData()