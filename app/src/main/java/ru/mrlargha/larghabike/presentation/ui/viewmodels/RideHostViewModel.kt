package ru.mrlargha.larghabike.presentation.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import ru.mrlargha.larghabike.domain.interactors.TrackingInteractor
import ru.mrlargha.larghabike.model.domain.GPSState
import ru.mrlargha.larghabike.model.presentation.TrackerDataWithPath
import ru.mrlargha.larghabike.rx.ISchedulersFacade
import javax.inject.Inject

/**
 * ViewModel для RideHostActivity
 *
 * @property trackingInteractor
 * @property schedulersFacade
 */
@HiltViewModel
class RideHostViewModel @Inject constructor(
    private val trackingInteractor: TrackingInteractor,
    private val schedulersFacade: ISchedulersFacade
) :
    ViewModel() {

    companion object {
        private const val TAG = "RideHostViewModel"
    }

    private val compositeDisposable = CompositeDisposable()

    /**
     * Текущие данные трэкера
     */
    val trackerData: MutableLiveData<TrackerDataWithPath> = MutableLiveData()

    /**
     * ID поездки
     *
     * Если значение появилось, значит запись данных о статистике закончена
     */
    val rideId: MutableLiveData<Long> = MutableLiveData()

    /**
     * Текущий статуст GPS
     */
    val gpsStatus: MutableLiveData<GPSState> = trackingInteractor.getGPSState()

    /**
     * Текущий маршрут
     */
    val track: MutableLiveData<List<LatLng>> = MutableLiveData()

    init {
        compositeDisposable.addAll(
            trackingInteractor.trackerDataObservable.subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui()).subscribe {
                    trackerData.postValue(it)
                },

            trackingInteractor.pathSource
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe({
                    track.postValue(it)
                }, {
                    Log.w(TAG, "Unable to get path", it)
                }),

            trackingInteractor.rideIdSource.subscribe {
                rideId.value = it
            }
        )
    }

    override fun onCleared() {
        Log.i(TAG, "onCleared: cleared")
        compositeDisposable.clear()
        super.onCleared()
    }
}