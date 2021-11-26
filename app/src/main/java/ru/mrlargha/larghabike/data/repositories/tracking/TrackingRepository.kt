package ru.mrlargha.larghabike.data.repositories.tracking

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import ru.mrlargha.larghabike.data.repositories.tracking.calculation.GPSCalculationUtil
import ru.mrlargha.larghabike.data.repositories.tracking.calculation.GPSLocationFilter
import ru.mrlargha.larghabike.data.repositories.tracking.calculation.StatisticsCalculator
import ru.mrlargha.larghabike.domain.repos.IRideRepository
import ru.mrlargha.larghabike.domain.repos.ITrackingRepository
import ru.mrlargha.larghabike.model.domain.*
import ru.mrlargha.larghabike.rx.ISchedulersFacade
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Репозиторий, осуществляющий основные операции по трэкингу маршрута
 *
 * @property rideRepository репозиторий поездок
 * @property statisticsCalculator калькулятор статистики
 * @property schedulersFacade фасад над Java RX Schedulers
 */
@Singleton
class TrackingRepository @Inject constructor(
    private val rideRepository: IRideRepository,
    private val statisticsCalculator: StatisticsCalculator,
    private val schedulersFacade: ISchedulersFacade
) : ITrackingRepository {

    companion object {
        const val TAG = "TrackingRepository"
    }

    /**
     * Hot observable для текущих показателей трекера
     */
    override val trackerDataObservable: Observable<TrackerData> =
        Observable.interval(1, TimeUnit.SECONDS).map { getCurrentTrackerData() }.share()

    /**
     * Текущий режим трэкера
     */
    override val currentTrackingModeLiveData: MutableLiveData<TrackingMode> = MutableLiveData()

    /**
     * Текущее состояние GPS
     */
    override val currentGPSStateLiveData: MutableLiveData<GPSState> = MutableLiveData()

    /**
     * Текущий режим трэкера
     * Установка значения обновит [currentTrackingModeLiveData]
     */
    override var currentTrackingMode = TrackingMode.STOPPED
        set(value) {
            field = value
            currentTrackingModeLiveData.postValue(value)
        }

    /**
     * Текущее состояние GPS
     * Установка значения обновит [currentGPSStateLiveData]
     */
    override var currentGPSState = GPSState.DISABLED
        set(value) {
            field = value
            currentGPSStateLiveData.postValue(value)
        }


    private var lastProceededLocation: Location? = null
    private var newLocation: Location? = null

    private var rideId = 0L
    private lateinit var currentBike: Bike

    private var skippedLocationUpdates = 0

    /**
     * Начать трэкинг поездки
     *
     * @param rideId id поездки
     * @param bike велосипед на котором мы едем
     */
    override fun beginTracking(rideId: Long, bike: Bike) {
        if (currentTrackingMode == TrackingMode.STOPPED) {
            Log.i(TAG, "Begin tracking with rideID: $rideId")
            this.rideId = rideId
            statisticsCalculator.reset()
            currentBike = bike
        }
    }

    /**
     * Остановить трэкинг
     */
    override fun stopTracking() {
        currentTrackingMode = TrackingMode.STOPPED
        Log.i(TAG, "stopTracking: Tracking stopped")
    }

    /**
     * Добавить новую локацию для отслеживания
     *
     * @param location новая локация
     */
    override fun postNewGPSData(location: Location) {
        if (currentGPSState != GPSState.FIX) {
            currentGPSState = GPSState.FIX
        }
        if (currentTrackingMode == TrackingMode.AWAITING) {
            currentTrackingMode = TrackingMode.GPS
        }
        skippedLocationUpdates = 0

        val filteredLocation = GPSLocationFilter.filterLocation(location)
        newLocation = filteredLocation
    }

    /**
     * Получить последний кадр статистики
     *
     * @return последний кадр статистики или `null` если он не был записан
     */
    override fun getLastStatisticFrame(): StatisticFrame? {
        return statisticsCalculator.getLastStatisticFrame()
    }

    private fun getCurrentTrackerData(): TrackerData {
        when (currentTrackingMode) {
            TrackingMode.GPS -> {
                if (lastProceededLocation == newLocation) {
                    skippedLocationUpdates += 1
                    if (skippedLocationUpdates >= 10) {

                        Log.w(
                            TAG,
                            "getCurrentTrackerData: no data for 10 seconds, switching to AWAITING_DATA mode"
                        )
                        currentGPSState = GPSState.AWAITING_DATA
                        currentTrackingMode = TrackingMode.AWAITING
                    }
                }

                // TODO move calculation move this to another class
                val location = newLocation
                val distanceDelta = lastProceededLocation?.distanceTo(location) ?: 0f
                val timeDelta = location?.time?.minus(lastProceededLocation?.time ?: 0) ?: 0

                location?.let {
                    val telemetryFrame = TelemetryFrame(
                        time = Date(),
                        rideId = rideId,
                        currentSpeed = location.speed,
                        distanceDelta = if (location.speed != 0f) distanceDelta else 0f,
                        rotationsPerMinute = GPSCalculationUtil.calculateRPM(
                            distanceDelta,
                            timeDelta,
                            currentBike
                        ).roundToInt(),
                        currentCadence = null,
                        latitude = it.latitude,
                        longitude = it.longitude
                    )

                    val statisticFrame = statisticsCalculator.addFrame(telemetryFrame)
                    val trackerData = TrackerData(telemetryFrame, statisticFrame)
                    Log.d(TAG, "createTrackerData: data: $trackerData")

                    lastProceededLocation = location

                    return trackerData
                }
            }

            else -> {
                Log.w(TAG, "createTrackerData: GPS is not available")
            }
        }
        // Затычка для того чтобы корректно отображася таймер поездки
        return TrackerData(null, statisticsCalculator.addEmptyFrame())
    }
}