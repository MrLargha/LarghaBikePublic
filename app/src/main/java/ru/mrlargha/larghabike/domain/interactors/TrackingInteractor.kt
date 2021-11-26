package ru.mrlargha.larghabike.domain.interactors

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import ru.mrlargha.larghabike.domain.repos.IBikeRepository
import ru.mrlargha.larghabike.domain.repos.IRideRepository
import ru.mrlargha.larghabike.domain.repos.ITrackingRepository
import ru.mrlargha.larghabike.model.domain.Bike
import ru.mrlargha.larghabike.model.domain.GPSState
import ru.mrlargha.larghabike.model.domain.Ride
import ru.mrlargha.larghabike.model.domain.TrackingMode
import ru.mrlargha.larghabike.model.presentation.TrackerDataWithPath
import ru.mrlargha.larghabike.rx.ISchedulersFacade
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Интеракторя для трэкинга поездок
 *
 * @property trackingRepository репозиторий для трэкинга поездок
 * @property rideRepository репозиторий для хранения поездок
 * @property bikeRepository репозиторий с велосипедами
 * @property schedulersFacade фасад на Java RX Schedulers
 */
@Singleton
class TrackingInteractor @Inject constructor(
    private val trackingRepository: ITrackingRepository,
    private val rideRepository: IRideRepository,
    private val bikeRepository: IBikeRepository,
    private val schedulersFacade: ISchedulersFacade
) {

    companion object {
        private const val TAG = "TrackingInteractor"
    }

    private val compositeDisposable = CompositeDisposable()

    /**
     * ID поездки. Значение устанавливается когда завершается отслеживание.
     */
    val rideIdSource: PublishSubject<Long> = PublishSubject.create()

    val trackerDataObservable: PublishSubject<TrackerDataWithPath> = PublishSubject.create()

    val pathSource: PublishSubject<List<LatLng>> = PublishSubject.create()

    /**
     * Начать поездку
     */
    @SuppressLint("CheckResult")
    fun startRide(): Single<Pair<Long, Bike>> {
        return rideRepository.getCurrentRide()
            .toSingle(Ride(isCurrentRide = true))
            .flatMap { rideRepository.saveRide(it) }
            .flatMap { rideId: Long ->
                bikeRepository.getSelectedBike()
                    .map { bike -> Pair(rideId, bike) }
            }
            .doOnSuccess { (rideId, bike) ->
                trackingRepository.beginTracking(rideId, bike)
                val disposable =
                    trackingRepository.trackerDataObservable
                        .flatMapSingle {
                            if (it.telemetryFrame != null)
                                rideRepository.addFrame(it.telemetryFrame)
                                    .andThen(Single.just(it))
                            else
                                Single.just(it)
                        }.flatMap { trackerData ->
                            rideRepository.getAllFramesByRide(rideId).map { list ->
                                TrackerDataWithPath(
                                    trackerData.telemetryFrame,
                                    trackerData.statisticFrame,
                                    list.mapNotNull { it.getLatLng() })
                            }.toObservable()
                        }.subscribe {
                            trackerDataObservable.onNext(it)
                        }
                compositeDisposable.addAll(disposable)
            }
    }


    /**
     * Завершить трэкинг поездки
     */
    fun finishRide() {
        trackingRepository.stopTracking()
        trackingRepository.getLastStatisticFrame()?.let { statisticFrame ->
            rideRepository.getCurrentRide()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe { ride ->
                    ride.setDataFromStatisticFrame(statisticFrame)
                    ride.isCurrentRide = false
                    compositeDisposable.add(rideRepository.updateRide(ride)
                        .subscribeOn(schedulersFacade.io())
                        .observeOn(schedulersFacade.ui())
                        .subscribe {
                            rideIdSource.onNext(ride.id)
                            compositeDisposable.clear()
                        })
                }
        }
    }

    /**
     * Обновить статус доступность геолокации
     *
     * @param isLocationAvailable доступна ли локация
     */
    fun notifyLocationStatusChanged(isLocationAvailable: Boolean) {
        if (!isLocationAvailable)
            trackingRepository.currentGPSState = GPSState.NOT_AVAILABLE
        trackingRepository.currentTrackingMode = TrackingMode.AWAITING
    }

    /**
     * Обновить текущую локацию телефона
     *
     * @param location новая локация
     */
    fun notifyLocationChanged(location: Location) {
        trackingRepository.postNewGPSData(location)
    }

    /**
     * Получить LiveData с состоянием GPS
     *
     * @return [GPSState]
     */
    fun getGPSState(): MutableLiveData<GPSState> {
        return trackingRepository.currentGPSStateLiveData
    }
}