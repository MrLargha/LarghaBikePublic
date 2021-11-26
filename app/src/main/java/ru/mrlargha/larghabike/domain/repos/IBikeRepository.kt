package ru.mrlargha.larghabike.domain.repos

import io.reactivex.Observable
import io.reactivex.Single
import ru.mrlargha.larghabike.model.domain.Bike
import java.io.File

/**
 * Репозиторий для работы с велосипедами
 */
interface IBikeRepository {
    /**
     * Вставить велосипед в базу
     *
     * @param bike велосипед для вставки
     * @return [Single] с id вставленного велосипеда
     */
    fun saveBike(bike: Bike): Single<Long>

    /**
     * Получить велосипед по его ID
     *
     * @param bikeId id велосипеда
     * @return [Single] с объектом [Bike]
     */
    fun getBikeById(bikeId: Long): Single<Bike>

    /**
     * Получить список всех велосипедов
     *
     * @return [Single] со списком велосипедов
     */
    fun getAllBikes(): Observable<List<Bike>>

    /**
     * Получить выбранный пользователем велосипед
     *
     * @return [Single] c велосипедом
     */
    fun getSelectedBike(): Single<Bike>

    /**
     * Выбрать велосипед
     *
     * @param bikeId велосипед, который нужно выбрать
     */
    fun selectBike(bikeId: Long)

    /**
     * Получить файл для сохранения картинки
     *
     * @return файл
     */
    fun getFileForNewBikeImage(): File

    /**
     * Проверяет доступен ли хотя бы один велоспид для поездки
     *
     * @return `true` если велосипед доступен, `false` - если нет
     */
    fun hasAvailableBike(): Boolean
}