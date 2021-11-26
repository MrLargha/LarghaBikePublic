package ru.mrlargha.larghabike.presentation.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mrlargha.larghabike.domain.repos.IBikeRepository
import ru.mrlargha.larghabike.model.domain.Bike
import ru.mrlargha.larghabike.rx.ISchedulersFacade
import javax.inject.Inject

/**
 * ViewModel для BikeSelectActivity
 *
 * @property bikeRepository репозиторий с велосипедами
 * @property schedulers фасад над JavaRX Schedulers
 */
@HiltViewModel
class BikeSelectViewModel @Inject constructor(
    private val bikeRepository: IBikeRepository,
    private val schedulers: ISchedulersFacade
) : BaseViewModel() {

    companion object {
        private const val TAG = "BikeSelectViewModel"
    }

    /**
     * Список велосипедов
     */
    val bikesListLiveData = MutableLiveData<List<Bike>>()

    /**
     * Выбранный велосипед
     */
    val selectedBikeLiveData = MutableLiveData<Bike>()

    init {
        compositeDisposable.add(
            bikeRepository.getSelectedBike()
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .subscribe({ bike ->
                    selectedBikeLiveData.value = bike
                }, {
                    Log.w(TAG, "Can't load selected bike", it)
                })
        )
        compositeDisposable.add(
            bikeRepository.getAllBikes()
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .subscribe({
                    bikesListLiveData.value = it
                }, {
                    Log.w(TAG, "Can't load bikes list", it)
                })
        )
    }

    /**
     * Выбрать велосипед
     *
     * @param bike велосипед, который надо выбрать
     */
    fun selectBike(bike: Bike) {
        bikeRepository.selectBike(bike.id)
    }
}