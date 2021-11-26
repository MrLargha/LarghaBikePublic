package ru.mrlargha.larghabike.domain.repos

import android.location.Location
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import ru.mrlargha.larghabike.model.domain.*

interface ITrackingRepository {
    /**
     * Текущее состояние GPS
     */
    val currentGPSStateLiveData: MutableLiveData<GPSState>

    /**
     * Текущий режим трэкера
     * Установка значения обновит [currentTrackingModeLiveData]
     */
    var currentTrackingMode: TrackingMode

    /**
     * Текущий режим трэкера
     */
    val currentTrackingModeLiveData: MutableLiveData<TrackingMode>

    /**
     * Текущее состояние GPS
     * Установка значения обновит [currentGPSStateLiveData]
     */
    var currentGPSState: GPSState

    /**
     * Hot observable для текущих показателей трекера
     */
    val trackerDataObservable: Observable<TrackerData>

    /**
     * Начать трэкинг поездки
     *
     * @param rideId id поездки
     * @param bike велосипед на котором мы едем
     */
    fun beginTracking(rideId: Long, bike: Bike)

    /**
     * Остановить трэкинг
     */
    fun stopTracking()

    /**
     * Добавить новую локацию для отслеживания
     *
     * @param location новая локация
     */
    fun postNewGPSData(location: Location)

    /**
     * Получить последний кадр статистики
     *
     * @return последний кадр статистики или `null` если он не был записан
     */
    fun getLastStatisticFrame(): StatisticFrame?
}