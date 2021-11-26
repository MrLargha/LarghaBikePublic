package ru.mrlargha.larghabike.presentation.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mrlargha.larghabike.domain.interactors.RideStatisticInteractor
import ru.mrlargha.larghabike.domain.repos.IBikeRepository
import javax.inject.Inject

/**
 * ViewModel для MainActivity
 *
 * @property rideStatisticInteractor интерактор для работы
 * @property bikeRepository репозиторий для работы с велосипедами
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val rideStatisticInteractor: RideStatisticInteractor,
    private val bikeRepository: IBikeRepository
) : ViewModel() {

    /**
     * Список завершенных поездок
     */
    val rides = rideStatisticInteractor.getFinishedRides()

    /**
     * Доступен ли велосипед для поездки
     *
     * @return true, если доступен, false - в ином случае
     */
        // livedata
    fun hasAvailableBike(): Boolean {
        return bikeRepository.hasAvailableBike()
    }

}