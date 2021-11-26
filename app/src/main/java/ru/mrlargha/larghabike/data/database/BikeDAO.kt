package ru.mrlargha.larghabike.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Observable
import io.reactivex.Single
import ru.mrlargha.larghabike.model.data.BikeEntity

/**
 * DAO для работы с объектами [BikeEntity]
 */
@Dao
interface BikeDAO {
    /**
     * Вставить велосипед в базу
     *
     * @param bike велосипед для вставки
     * @return Single с id вставленного велосипеда
     */
    @Insert
    fun addBike(bike: BikeEntity): Single<Long>

    /**
     * Получить список всех велосипедов
     *
     * @return Single со списком велосипедов
     */
    @Query("SELECT * FROM bike")
    fun getAllBikes(): Observable<List<BikeEntity>>

    /**
     * Получить велосипед по его ID
     *
     * @param bikeId id велосипеда
     * @return Single с объектом [BikeEntity]
     */
    @Query("SELECT * FROM bike WHERE id = :bikeId")
    fun getBikeById(bikeId: Long): Single<BikeEntity>
}