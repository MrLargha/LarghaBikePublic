package ru.mrlargha.larghabike.model.domain

/**
 * Состояние GPS
 */
enum class GPSState {
    FIX,
    AWAITING_DATA,
    NOT_AVAILABLE,
    DISABLED
}