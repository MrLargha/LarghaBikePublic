package ru.mrlargha.larghabike.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity предствляющая велосипед в базе данных
 *
 * @property id id велосипеда
 * @property name название велосипеда
 * @property wheelDiameter диаметр колеса велосипеда
 * @property photoPath путь к фотографии велосипеда
 */
@Entity(tableName = "bike")
data class BikeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val wheelDiameter: Float,
    val photoPath: String? = null
)