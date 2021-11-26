package ru.mrlargha.larghabike.rx

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Фасад для оборачивания JavaRX [Schedulers] и [AndroidSchedulers]
 */
interface ISchedulersFacade {
    /**
     * Scheduler работающий на UI-потоке
     */
    fun ui(): Scheduler

    /**
     * Scheduler для операций ввода-вывода
     */
    fun io(): Scheduler

    /**
     * Scheduler для вычислений
     */
    fun computation(): Scheduler
}