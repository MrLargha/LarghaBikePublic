package ru.mrlargha.larghabike.data.repositories.bike

import io.reactivex.Observable
import io.reactivex.Single
import ru.mrlargha.larghabike.data.database.BikeDAO
import ru.mrlargha.larghabike.data.repositories.settings.SettingsRepository
import ru.mrlargha.larghabike.data.util.FilesystemProvider
import ru.mrlargha.larghabike.domain.repos.IBikeRepository
import ru.mrlargha.larghabike.model.converters.FromBikeToBikeEntity
import ru.mrlargha.larghabike.model.converters.TwoWayConverter
import ru.mrlargha.larghabike.model.data.BikeEntity
import ru.mrlargha.larghabike.model.domain.Bike
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * Реализация репозитория для работы с велосипедами
 *
 * @property filesystemProvider провайдер файловой системы
 * @property bikeDAO для работы с [Bike] в базе данных
 * @property settingsRepository провайдер настроек приложения
 */
class BikeRepository @Inject constructor(
    private val filesystemProvider: FilesystemProvider,
    private val bikeDAO: BikeDAO,
    private val settingsRepository: SettingsRepository
) : IBikeRepository {

    /**
     * Методы задокументированы в интерфейсы
     */

    private val converter: TwoWayConverter<Bike, BikeEntity> = FromBikeToBikeEntity()

    override fun saveBike(bike: Bike): Single<Long> {
        return bike
            .let(converter::convert)
            .let(bikeDAO::addBike)
    }

    override fun getAllBikes(): Observable<List<Bike>> {
        return bikeDAO.getAllBikes().map {
            it.map(converter::convertReverse)
        }
    }

    override fun getBikeById(bikeId: Long): Single<Bike> {
        return bikeDAO.getBikeById(bikeId).map(converter::convertReverse)
    }

    override fun getSelectedBike(): Single<Bike> = getBikeById(settingsRepository.getSelectedBikeId())

    override fun selectBike(bikeId: Long) {
        settingsRepository.setSelectedBikeId(bikeId)
    }

    override fun getFileForNewBikeImage(): File {
        val dir = File(filesystemProvider.getExternalStorage("image"), "bikes")
        dir.mkdirs()
        return File(
            dir,
            UUID.randomUUID().toString().replace("-", "") + ".png"
        ).also { it.createNewFile() }
    }

    override fun hasAvailableBike(): Boolean {
        return settingsRepository.getSelectedBikeId() != -1L
    }
}