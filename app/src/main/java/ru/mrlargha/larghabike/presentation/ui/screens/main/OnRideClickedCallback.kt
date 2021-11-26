package ru.mrlargha.larghabike.presentation.ui.screens.main

import ru.mrlargha.larghabike.model.domain.Ride

fun interface OnRideClickedCallback {
    fun rideClicked(ride: Ride)
}