package ru.mrlargha.larghabike.data.repositories.tracking.calculation

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import ru.mrlargha.larghabike.model.domain.Bike


class GPSCalculationUtilTest {

    @Test
    fun calculateRPM() {
        dataset.forEach {
            val (distance, time, bike, result) = it
            assertThat(
                GPSCalculationUtil.calculateRPM(
                    distance as Float,
                    time as Long,
                    bike as Bike
                )
            // Большая точность нам ни к чему, всё равно до целых будем округлять потом
            ).isWithin(0.01f).of(result as Float)
        }
    }

    companion object {
        val dataset = listOf(
            // Расстояние, время, велосипед, ожидаемая скорость
            // https://www.center-pss.ru/klk/k966.htm - тут посчитал данные
            // Дюймы в метры через гугл переводил
            listOf(10f, 1000L, Bike(1, "Kek", 21f), 179.026932616f * 2),
            listOf(10f, 1000L, Bike(1, "Kek", 28f), 134.270199462f * 2),
            listOf(100f, 1000L, Bike(1, "Kek", 28f), 1342.701994626f * 2),
        )
    }
}