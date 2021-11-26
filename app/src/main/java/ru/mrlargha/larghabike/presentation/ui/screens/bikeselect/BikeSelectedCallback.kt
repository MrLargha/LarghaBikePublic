package ru.mrlargha.larghabike.presentation.ui.screens.bikeselect

import ru.mrlargha.larghabike.model.domain.Bike


fun interface BikeSelectedCallback {
    fun selected(bike: Bike)
}
