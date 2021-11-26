package ru.mrlargha.larghabike.domain.interactors

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import io.reactivex.Single
import ru.mrlargha.larghabike.data.util.Formatters
import ru.mrlargha.larghabike.domain.charts.ChartsDataCreator
import ru.mrlargha.larghabike.domain.repos.IMapPreviewRepository
import ru.mrlargha.larghabike.domain.repos.IRideRepository
import ru.mrlargha.larghabike.extensions.toKm
import ru.mrlargha.larghabike.extensions.toKmH
import ru.mrlargha.larghabike.model.domain.Ride
import ru.mrlargha.larghabike.model.domain.TelemetryFrame
import ru.mrlargha.larghabike.model.presentation.ridestat.AbstractRideDetailCardData
import ru.mrlargha.larghabike.model.presentation.ridestat.OneValueCardData
import ru.mrlargha.larghabike.model.presentation.ridestat.SpeedChartCardData
import javax.inject.Inject

/**
 * Интерактор для работы со статистикой по поездкам
 *
 * @property rideRepository репозиторий по работе с поездками
 * @property mapPreviewRepository репозитория для работы с отобржанием маршрута на карте
 * @property chartsDataCreator создатель данных для построения графиков
 */
class RideStatisticInteractor @Inject constructor(
    private val rideRepository: IRideRepository,
    private val mapPreviewRepository: IMapPreviewRepository,
    private val chartsDataCreator: ChartsDataCreator
) {

    /**
     * Получить список всех завершённых поездок
     *
     * @return список завершённых поездок
     */
    fun getFinishedRides(): LiveData<List<Ride>> = rideRepository.getAllFinishedRides()


    /**
     * Получить картинку с картой и маршрутом поездки на ней
     *
     * @param rideId
     * @return Single<Bitmap> с картинкой
     */
    fun getMapPreviewForRide(rideId: Long): Single<Bitmap> {
        return rideRepository.getRideWithFrames(rideId)
            .flatMap { mapPreviewRepository.getMapForRide(it.ride, it.frames) }
    }

    /**
     * Получить список [AbstractRideDetailCardData] для [Ride] с указанным id
     *
     * @param rideId id поездки
     * @return Single со списком данных для карточек статистики по поездке
     */
    fun getStatisticCardsListForRide(rideId: Long): Single<List<AbstractRideDetailCardData>> {
        return rideRepository.getRideWithFrames(rideId)
            .map { createRideSingleValueCardSet(it.ride) + createChartsCards(it.ride, it.frames) }
    }

    private fun createChartsCards(
        ride: Ride,
        frames: List<TelemetryFrame>
    ): List<AbstractRideDetailCardData> {
        val speedChartData = chartsDataCreator.createLineChartData(frames) { it.currentSpeed }
        val avSpeedLineData = chartsDataCreator.createAvSpeedBaselineData(
            ride.avMoveSpeed,
            ride.startTime.time,
            ride.endTime.time
        )
        return listOf(
            SpeedChartCardData(
                "Speed",
                "Detail chart of your speed",
                speedChartData,
                avSpeedLineData
            )
        )
    }

    private fun createRideSingleValueCardSet(ride: Ride): List<AbstractRideDetailCardData> {
        return listOf(
            OneValueCardData(
                "Average Speed",
                Formatters.defaultNumberFormat.format(ride.avSpeed.toKmH()),
                "km/h",
                "Distance divided by all ride time",
            ),
            OneValueCardData(
                "Average Moving Speed",
                Formatters.defaultNumberFormat.format(ride.avMoveSpeed.toKmH()),
                "km/h",
                "Distance divided by move time (TM)",
            ),
            OneValueCardData(
                "Maximum Speed",
                Formatters.defaultNumberFormat.format(ride.maxSpeed.toKmH()),
                "km/h",
                "",
            ),
            OneValueCardData(
                "Total Distance",
                Formatters.defaultNumberFormat.format(ride.distance.toKm()),
                "km",
                "Total distance driven",
            ),
            OneValueCardData(
                "Total Time",
                Formatters.formatTimeHoursMinutes((ride.getDurationMinutes() * 60 * 1000).toLong()),
                "",
                "Time from start to finish",
            ),
            OneValueCardData(
                "Move Time",
                Formatters.formatTimeHoursMinutes((ride.getMoveTimeMinutes() * 60 * 1000).toLong()),
                "",
                "Time in motion",
            )
        )
    }
}