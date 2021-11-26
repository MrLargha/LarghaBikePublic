package ru.mrlargha.larghabike.data.repositories.mappreview

import com.google.android.gms.maps.model.LatLng

/**
 * Кодировщик пути в строку для формирования запросов к GoogleMapsStaticAPI
 */
object GMapsStaticAPIPathEncoder {
    /**
     * Закодировать маршрут
     *
     * @param path путь в виде списка объектов [LatLng]
     * @param color цвет, принимаются значения из списка: {black, brown, green, purple, yellow, blue, gray, orange, red, white}
     * @param weight толщина линии маршрута
     * @return закодированный в строку путь
     */
    fun encode(path: List<LatLng>, color: String = "red", weight: Int = 5): String {
        return "color:$color|weight:$weight|" + path.joinToString(separator = "|") {
            "${it.latitude},${it.longitude}"
        }
    }
}