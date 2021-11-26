package ru.mrlargha.larghabike.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.mrlargha.larghabike.model.data.RideEntity
import ru.mrlargha.larghabike.model.data.RideWithFramesEntity
import ru.mrlargha.larghabike.model.data.TelemetryFrameEntity

/**
 * DAO для работы с объектами [RideEntity], [TelemetryFrameEntity] и [RideWithFramesEntity]
 */
@Dao
interface RideDAO {
    /**
     * Получить список из всех поездок
     *
     * ПРИМЕЧАНИЕ: для того чтобы в livedata приходили изменения, доступ к базе нужно осуществлят
     * из одного и того же экземпляра DAO
     *
     * @return LiveData со списком поездок
     */
    @Query("SELECT * FROM ride")
    fun getAllRides(): LiveData<List<RideEntity>>

    /**
     * Обновить поездку
     *
     * @param ride поездка для обновления
     * @return [Completable]
     */
    @Update
    fun updateRide(ride: RideEntity): Completable

    /**
     * Вствить поездку в базу. Если поездка с таким ID уже была в базе, то она будет перезаписана
     *
     * @param ride поездка для вставки
     * @return [Single] с ID поздки
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRide(ride: RideEntity): Single<Long>

    /**
     * Получить список завершённых поездок
     *
     * @return [LiveData] со списком поездок
     */
    @Query("SELECT * FROM ride WHERE completed")
    fun getAllFinishedRides(): LiveData<List<RideEntity>>

    /**
     * Получить поезку по её ID
     *
     * @param rideId id поездки
     * @return [Single] с объектом [RideEntity]
     */
    @Query("SELECT * FROM ride WHERE id = :rideId LIMIT 1")
    fun getRideById(rideId: Long): Single<RideEntity>

    /**
     * Получить поездку и список записанной во время неё телеметрии
     *
     * @param rideId id поездки
     * @return [Single] с объектом [RideWithFramesEntity]
     */
    @Query("SELECT * FROM ride WHERE id = :rideId LIMIT 1")
    fun getRideWithFramesById(rideId: Long): Single<RideWithFramesEntity>

    /**
     * Вставить кадр телеметрии
     *
     * @param frame кадр для вставки
     * @return [Completable]
     */
    @Insert
    fun insertTelemetryFrame(frame: TelemetryFrameEntity): Completable

    /**
     * Получить всю телеметрию на конкретную поездку
     *
     * @param rideId id поездки
     * @return [Flowable] со списком объектов [TelemetryFrameEntity]
     */
    @Query("SELECT * FROM telemetry_frame WHERE rideId=:rideId")
    fun getAllFramesByRide(rideId: Long): Single<List<TelemetryFrameEntity>>

    /**
     * Получить текущую поездку
     *
     * @return текущая поездка
     */
    @Query("SELECT * FROM ride WHERE isCurrentRide LIMIT 1")
    fun getCurrentRide(): Maybe<RideEntity>
}