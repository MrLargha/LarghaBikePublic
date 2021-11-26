package ru.mrlargha.larghabike.extensions

import android.location.Location
import com.google.android.gms.maps.model.LatLng

/**
 * Перевести [Location] в [LatLng]
 *
 * @return [LatLng]
 */
fun Location.toLatLng(): LatLng = LatLng(this.latitude, this.longitude)
