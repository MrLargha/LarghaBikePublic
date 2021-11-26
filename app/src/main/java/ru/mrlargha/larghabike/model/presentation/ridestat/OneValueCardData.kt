package ru.mrlargha.larghabike.model.presentation.ridestat

/**
 * Данные для карточки с 1 значеним
 *
 * @property title заголовок карточки
 * @property value значение
 * @property unit единицы измерения
 * @property subtitle подпись
 */
class OneValueCardData(
    val title: String,
    val value: String,
    val unit: String,
    val subtitle: String,
) : AbstractRideDetailCardData()