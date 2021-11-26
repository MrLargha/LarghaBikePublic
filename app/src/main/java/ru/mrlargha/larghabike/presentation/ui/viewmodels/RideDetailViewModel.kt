package ru.mrlargha.larghabike.presentation.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mrlargha.larghabike.domain.interactors.RideStatisticInteractor
import ru.mrlargha.larghabike.model.presentation.ridestat.AbstractRideDetailCardData
import ru.mrlargha.larghabike.presentation.ui.screens.ridedetail.RideDetailActivity
import ru.mrlargha.larghabike.rx.ISchedulersFacade
import javax.inject.Inject

/**
 * ViewModel для RideDetailActivity
 *
 * @property rideStatisticInteractor интерактор для работы со статистикой по поездкам
 * @property savedStateHandle аргументы переданные активности
 * @property schedulersFacade фасад над JavaRX Schedulers
 */
@HiltViewModel
class RideDetailViewModel @Inject constructor(
    private val rideStatisticInteractor: RideStatisticInteractor,
    private val savedStateHandle: SavedStateHandle,
    private val schedulersFacade: ISchedulersFacade
) : BaseViewModel() {
    private val rideId: Long = savedStateHandle.get(RideDetailActivity.RIDE_ID_KEY) ?: -1

    /**
     * Картинка с картой и маршрутом поездки
     */
    val rideMapImageLiveData = MutableLiveData<Bitmap>()

    val errorLiveData = MutableLiveData<String>()

    /**
     * Данные карточек детализации маршрута
     */
    val cardDataListLiveData = MutableLiveData<List<AbstractRideDetailCardData>>()

    init {
        compositeDisposable.add(
            rideStatisticInteractor.getMapPreviewForRide(rideId)
                .subscribeOn(schedulersFacade.io()).observeOn(schedulersFacade.ui())
                .subscribe({ image ->
                    rideMapImageLiveData.value = (image)
                }, { e ->
                    errorLiveData.value = "Unable to load map preview."
                })
        )

        compositeDisposable.add(
            rideStatisticInteractor.getStatisticCardsListForRide(rideId)
                .subscribeOn(schedulersFacade.io()).observeOn(schedulersFacade.ui())
                .subscribe { list ->
                    cardDataListLiveData.value = (list)
                })
    }
}