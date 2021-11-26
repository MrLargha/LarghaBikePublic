package ru.mrlargha.larghabike.presentation.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import ru.mrlargha.larghabike.rx.ISchedulersFacade
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class RidePrepareViewModel @Inject constructor(
    private val schedulers: ISchedulersFacade
) :
    BaseViewModel() {

    companion object {
        const val TAG = "RidePrepareViewModel"
        const val DELAY = 5
    }

    val countDownLiveData: MutableLiveData<Int> = MutableLiveData()

    init {
        compositeDisposable.add(
            Observable.interval(1, TimeUnit.SECONDS).take(DELAY.toLong() + 1).map { DELAY - it }
                .subscribeOn(schedulers.computation())
                .observeOn(schedulers.ui()).subscribe {
                    countDownLiveData.value = it.toInt()
                }
        )
    }
}