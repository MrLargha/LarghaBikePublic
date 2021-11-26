package ru.mrlargha.larghabike.rx

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Фасад для оборачивания JavaRX [Schedulers] и [AndroidSchedulers]
 */
class SchedulersFacade @Inject constructor() : ISchedulersFacade {
    /**
     * Scheduler работающий на UI-потоке
     */
    override fun ui(): Scheduler = AndroidSchedulers.mainThread()

    /**
     * Scheduler для операций ввода-вывода
     */
    override fun io(): Scheduler = Schedulers.io()

    /**
     * Scheduler для вычислений
     */
    override fun computation(): Scheduler = Schedulers.computation()
}