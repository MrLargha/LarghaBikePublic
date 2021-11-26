package ru.mrlargha.larghabike.domain.repos

import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.mrlargha.larghabike.model.domain.Ride
import ru.mrlargha.larghabike.model.domain.RideWithFrames
import ru.mrlargha.larghabike.model.domain.TelemetryFrame

/**
 * Репозиторий для работы с объектами [Ride], [TelemetryFrame] и [RideWithFrames]
 */
interface IRideRepository {
    /**
     * Получить список из всех поездок
     *
     * @return LiveData со списком поездок
     */
    fun getAllRides(): LiveData<List<Ride>>

    /**
     * Вствить поездку в базу
     *
     * @param ride поездка для вставки
     * @return [Single] с ID поздки
     */
    fun saveRide(ride: Ride): Single<Long>

    /**
     * Обновить поездку
     *
     * @param ride поездка для обновления
     * @return [Completable]
     */
    fun updateRide(ride: Ride): Completable

    /**
     * Получить список завершённых поездок
     *
     * @return [LiveData] со списком поездок
     */
    fun getAllFinishedRides(): LiveData<List<Ride>>

    /**
     * Получить поезку по её ID
     *
     * @param rideId id поездки
     * @return [Single] с объектом [Ride]
     */
    fun getRideById(rideId: Long): Single<Ride>

    /**
     * Вставить кадр телеметрии
     *
     * @param frame кадр для вставки
     * @return [Completable]
     */
    fun addFrame(frame: TelemetryFrame): Completable

    /**
     * Получить поездку и список записанной во время неё телеметрии
     *
     * @param rideId id поездки
     * @return [Single] с объектом [RideWithFrames]
     */
    fun getRideWithFrames(rideId: Long): Single<RideWithFrames>

    /**
     * Получить всю телеметрию на конкретную поездку
     *
     * @param rideId id поездки
     * @return [Single] со списком объектов [TelemetryFrame]
     */
    fun getAllFramesByRide(rideId: Long): Single<List<TelemetryFrame>>


    /**
     * Получить текущую поездку
     *
     * @return текущая поездка
     */
    fun getCurrentRide(): Maybe<Ride>
}