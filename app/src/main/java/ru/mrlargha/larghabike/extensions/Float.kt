package ru.mrlargha.larghabike.extensions


/**
 * Конверитировать длину в метрах в километры
 *
 * @return километры
 */
fun Float.toKm(): Float {
    return this / 1000
}

/**
 * Конвертировать метры в секунду в километры в час
 *
 * @return скорость в километрах в час
 */
fun Float.toKmH(): Float {
    return this * 3.6f
}
