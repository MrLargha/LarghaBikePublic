package ru.mrlargha.larghabike.model.domain

/**
 * Модель предствляющая велосипед
 *
 * @property id id велосипеда
 * @property name название велосипеда
 * @property wheelDiameter диаметр колеса велосипеда
 * @property photoPath путь к фотографии велосипеда
 */
data class Bike(
    val id: Long = 0,
    val name: String,
    val wheelDiameter: Float,
    val photoPath: String? = null
)